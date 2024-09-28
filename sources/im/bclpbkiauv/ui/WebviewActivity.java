package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.WebviewActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.ShareAlert;
import java.net.URLEncoder;

public class WebviewActivity extends BaseFragment {
    private static final int TYPE_GAME = 0;
    private static final int TYPE_NONE = -1;
    private static final int TYPE_STAT = 1;
    private static final int open_in = 2;
    private static final int share = 1;
    /* access modifiers changed from: private */
    public String currentBot;
    private long currentDialogId;
    private String currentGame;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    /* access modifiers changed from: private */
    public String currentUrl;
    /* access modifiers changed from: private */
    public String linkToCopy;
    private boolean loadStats;
    /* access modifiers changed from: private */
    public String mTitle;
    /* access modifiers changed from: private */
    public ActionBarMenuItem progressItem;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public String short_param;
    /* access modifiers changed from: private */
    public int type;
    public Runnable typingRunnable = new Runnable() {
        public void run() {
            if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null && WebviewActivity.this.typingRunnable != null) {
                MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
                AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000);
            }
        }
    };
    /* access modifiers changed from: private */
    public WebView webView;

    private class WebviewProxy {
        private WebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable(eventName) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WebviewActivity.WebviewProxy.this.lambda$postEvent$0$WebviewActivity$WebviewProxy(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$postEvent$0$WebviewActivity$WebviewProxy(String eventName) {
            if (WebviewActivity.this.getParentActivity() != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(eventName);
                }
                char c = 65535;
                int hashCode = eventName.hashCode();
                if (hashCode != -1788360622) {
                    if (hashCode == 406539826 && eventName.equals("share_score")) {
                        c = 1;
                    }
                } else if (eventName.equals("share_game")) {
                    c = 0;
                }
                if (c == 0) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                } else if (c == 1) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                }
                WebviewActivity webviewActivity = WebviewActivity.this;
                webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, (String) null, false, WebviewActivity.this.linkToCopy, false));
            }
        }
    }

    public WebviewActivity(String url, String botName, String gameName, String startParam, MessageObject messageObject) {
        String str;
        this.currentUrl = url;
        this.currentBot = botName;
        this.currentGame = gameName;
        this.currentMessageObject = messageObject;
        this.short_param = startParam;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(this.currentBot);
        if (TextUtils.isEmpty(startParam)) {
            str = "";
        } else {
            str = "?game=" + startParam;
        }
        sb.append(str);
        this.linkToCopy = sb.toString();
        this.type = 0;
    }

    public WebviewActivity(String statUrl, long did) {
        this.currentUrl = statUrl;
        this.currentDialogId = did;
        this.type = 1;
    }

    public WebviewActivity(String url, String title) {
        this.currentUrl = url;
        this.mTitle = title;
        this.type = -1;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.webView.setLayerType(0, (Paint) null);
        this.typingRunnable = null;
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

    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WebviewActivity.this.finishFragment();
                } else if (id == 1) {
                    if (WebviewActivity.this.currentMessageObject != null) {
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                        WebviewActivity webviewActivity = WebviewActivity.this;
                        webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, (String) null, false, WebviewActivity.this.linkToCopy, false));
                    }
                } else if (id == 2) {
                    WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.progressItem = menu.addItemWithWidth(1, R.drawable.share, AndroidUtilities.dp(54.0f));
        int i = this.type;
        if (i == 0) {
            menu.addItem(0, (int) R.drawable.ic_ab_other).addSubItem(2, (int) R.drawable.msg_openin, (CharSequence) LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
            this.actionBar.setTitle(this.currentGame);
            ActionBar actionBar = this.actionBar;
            actionBar.setSubtitle("@" + this.currentBot);
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            this.progressItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
        } else if (i == 1) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_player_actionBar));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_player_actionBarItems), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_player_actionBarSelector), false);
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_player_actionBarTitle));
            this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
            this.actionBar.setTitle(LocaleController.getString("Statistics", R.string.Statistics));
            ContextProgressView contextProgressView2 = new ContextProgressView(context, 3);
            this.progressView = contextProgressView2;
            this.progressItem.addView(contextProgressView2, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        } else {
            if (this.mTitle != null) {
                this.actionBar.setTitle(this.mTitle);
            }
            ContextProgressView contextProgressView3 = new ContextProgressView(context, 3);
            this.progressView = contextProgressView3;
            this.progressItem.addView(contextProgressView3, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        }
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
            if (this.type == 0) {
                this.webView.addJavascriptInterface(new WebviewProxy(), "WebviewProxy");
            }
        }
        this.webView.setWebViewClient(new WebViewClient() {
            private boolean isInternalUrl(String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                Uri uri = Uri.parse(url);
                if (!"".equals(uri.getScheme())) {
                    return false;
                }
                if (WebviewActivity.this.type == 1) {
                    try {
                        WebviewActivity.this.reloadStats(Uri.parse(url.replace("hchat:statsrefresh", "hchat://m12345.com")).getQueryParameter("params"));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else {
                    WebviewActivity.this.finishFragment(false);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", uri);
                        intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                        intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                        ApplicationLoader.applicationContext.startActivity(intent);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (!isInternalUrl(url)) {
                    super.onLoadResource(view, url);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!isInternalUrl(url)) {
                    WebviewActivity.this.webView.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (WebviewActivity.this.progressView != null && WebviewActivity.this.progressView.getVisibility() == 0) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (WebviewActivity.this.type == 0) {
                        WebviewActivity.this.progressItem.getContentView().setVisibility(0);
                        WebviewActivity.this.progressItem.setEnabled(true);
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "alpha", new float[]{0.0f, 1.0f})});
                    } else {
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f})});
                    }
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WebviewActivity.this.type == 1) {
                                WebviewActivity.this.progressItem.setVisibility(8);
                            } else {
                                WebviewActivity.this.progressView.setVisibility(4);
                            }
                        }
                    });
                    animatorSet.setDuration(150);
                    animatorSet.start();
                }
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (WebviewActivity.this.mTitle == null && WebviewActivity.this.actionBar != null) {
                    WebviewActivity.this.actionBar.setTitle(title);
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
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
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

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        WebView webView2;
        if (isOpen && !backward && (webView2 = this.webView) != null) {
            webView2.loadUrl(this.currentUrl);
        }
    }

    public static boolean supportWebview() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (!"samsung".equals(manufacturer) || !"GT-I9500".equals(model)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void reloadStats(String params) {
        if (!this.loadStats) {
            this.loadStats = true;
            TLRPC.TL_messages_getStatsURL req = new TLRPC.TL_messages_getStatsURL();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.currentDialogId);
            req.params = params != null ? params : "";
            req.dark = Theme.getCurrentTheme().isDark();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WebviewActivity.this.lambda$reloadStats$1$WebviewActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadStats$1$WebviewActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WebviewActivity.this.lambda$null$0$WebviewActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$WebviewActivity(TLObject response) {
        this.loadStats = false;
        if (response != null) {
            WebView webView2 = this.webView;
            String str = ((TLRPC.TL_statsURL) response).url;
            this.currentUrl = str;
            webView2.loadUrl(str);
        }
    }

    public static void openGameInBrowser(String urlStr, MessageObject messageObject, Activity parentActivity, String short_name, String username) {
        String url;
        MessageObject messageObject2 = messageObject;
        String str = "";
        String url2 = urlStr;
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String existing = sharedPreferences.getString(str + messageObject.getId(), (String) null);
            StringBuilder hash = new StringBuilder(existing != null ? existing : str);
            StringBuilder addHash = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("hchat://share_game_score?hash=", "UTF-8"));
            if (existing == null) {
                char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    hash.append(chars[Utilities.random.nextInt(chars.length)]);
                }
            }
            addHash.append(hash);
            int index = url2.indexOf(35);
            if (index < 0) {
                url = url2 + "#" + addHash;
            } else {
                String curHash = url2.substring(index + 1);
                if (curHash.indexOf(61) < 0) {
                    if (curHash.indexOf(63) < 0) {
                        if (curHash.length() > 0) {
                            url = url2 + "?" + addHash;
                        } else {
                            url = url2 + addHash;
                        }
                    }
                }
                url = url2 + "&" + addHash;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(hash + "_date", (int) (System.currentTimeMillis() / 1000));
            SerializedData serializedData = new SerializedData(messageObject2.messageOwner.getObjectSize());
            messageObject2.messageOwner.serializeToStream(serializedData);
            editor.putString(hash + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            String str2 = hash + "_link";
            StringBuilder sb = new StringBuilder();
            sb.append("https://");
            sb.append(MessagesController.getInstance(messageObject2.currentAccount).linkPrefix);
            sb.append("/");
            try {
                sb.append(username);
                if (TextUtils.isEmpty(short_name)) {
                    String str3 = short_name;
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("?game=");
                    try {
                        sb2.append(short_name);
                        str = sb2.toString();
                    } catch (Exception e) {
                        e = e;
                        Activity activity = parentActivity;
                        FileLog.e((Throwable) e);
                    }
                }
                sb.append(str);
                editor.putString(str2, sb.toString());
                editor.commit();
                try {
                    Browser.openUrl((Context) parentActivity, url, false);
                    serializedData.cleanup();
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
                Activity activity2 = parentActivity;
                String str4 = short_name;
                FileLog.e((Throwable) e);
            }
        } catch (Exception e4) {
            e = e4;
            Activity activity3 = parentActivity;
            String str5 = short_name;
            String str6 = username;
            FileLog.e((Throwable) e);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        if (this.type == 0) {
            return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner2), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter2)};
        }
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBar), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarItems), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner4), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter4)};
    }
}
