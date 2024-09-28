package im.bclpbkiauv.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BottomPagesView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class IntroActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private int currentAccount = UserConfig.selectedAccount;
    private long currentDate;
    /* access modifiers changed from: private */
    public int currentViewPagerPage;
    private boolean destroyed;
    /* access modifiers changed from: private */
    public boolean dragging;
    /* access modifiers changed from: private */
    public int[] images;
    private boolean justCreated = false;
    /* access modifiers changed from: private */
    public boolean justEndDragging;
    /* access modifiers changed from: private */
    public int lastPage = 0;
    private LocaleController.LocaleInfo localeInfo;
    /* access modifiers changed from: private */
    public String[] messages;
    /* access modifiers changed from: private */
    public int startDragX;
    /* access modifiers changed from: private */
    public TextView startMessagingButton;
    private boolean startPressed = false;
    private TextView textView;
    /* access modifiers changed from: private */
    public String[] titles;
    /* access modifiers changed from: private */
    public ViewPager viewPager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        ScrollView scrollView;
        jumpIntro();
        setTheme(R.style.Theme_TMessages);
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", R.string.Page1Title), LocaleController.getString("Page2Title", R.string.Page2Title), LocaleController.getString("Page3Title", R.string.Page3Title), LocaleController.getString("Page4Title", R.string.Page4Title)};
        this.messages = new String[]{LocaleController.getString("Page1Message", R.string.Page1Message), LocaleController.getString("Page2Message", R.string.Page2Message), LocaleController.getString("Page3Message", R.string.Page3Message), LocaleController.getString("Page4Message", R.string.Page4Message)};
        this.images = new int[]{R.mipmap.img_intro_secure, R.mipmap.img_intro_secure2, R.mipmap.img_intro_group, R.mipmap.img_intro_private};
        ScrollView scrollView2 = new ScrollView(this);
        scrollView2.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-1);
        scrollView2.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        final ImageView introImg = new ImageView(this);
        introImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        frameLayout2.addView(introImg, LayoutHelper.createFrame(270, 270, 17));
        ViewPager viewPager2 = new ViewPager(this);
        this.viewPager = viewPager2;
        viewPager2.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                IntroActivity.this.bottomPages.setPageOffset(position, positionOffset);
                float width = (float) IntroActivity.this.viewPager.getMeasuredWidth();
                if (width != 0.0f) {
                    introImg.setAlpha(1.0f - Math.abs((((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) IntroActivity.this.currentViewPagerPage) * width)) / width));
                }
            }

            public void onPageSelected(int i) {
                int unused = IntroActivity.this.currentViewPagerPage = i;
                introImg.setImageResource(IntroActivity.this.images[IntroActivity.this.currentViewPagerPage]);
                IntroActivity.this.startMessagingButton.setVisibility(i == 3 ? 0 : 8);
            }

            public void onPageScrollStateChanged(int i) {
                if (i == 1) {
                    boolean unused = IntroActivity.this.dragging = true;
                    IntroActivity introActivity = IntroActivity.this;
                    int unused2 = introActivity.startDragX = introActivity.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
                } else if (i == 0 || i == 2) {
                    if (IntroActivity.this.dragging) {
                        boolean unused3 = IntroActivity.this.justEndDragging = true;
                        boolean unused4 = IntroActivity.this.dragging = false;
                    }
                    if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                        IntroActivity introActivity2 = IntroActivity.this;
                        int unused5 = introActivity2.lastPage = introActivity2.viewPager.getCurrentItem();
                    }
                }
            }
        });
        introImg.setImageResource(this.images[this.currentViewPagerPage]);
        TextView textView2 = new TextView(this);
        this.startMessagingButton = textView2;
        textView2.setText(LocaleController.getString("StartMessaging", R.string.StartMessaging).toUpperCase());
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTextColor(-1);
        this.startMessagingButton.setTextSize(1, 16.0f);
        this.startMessagingButton.setVisibility(8);
        this.startMessagingButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(24.0f), Color.parseColor("#FF268CFF"), Color.parseColor("#FF1E69BD")));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            scrollView = scrollView2;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.startMessagingButton.setStateListAnimator(animator);
        } else {
            scrollView = scrollView2;
        }
        this.startMessagingButton.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        frameLayout.addView(this.startMessagingButton, LayoutHelper.createFrame(-2.0f, -2.0f, 81, 10.0f, 0.0f, 10.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                IntroActivity.this.lambda$onCreate$0$IntroActivity(view);
            }
        });
        if (BuildVars.DEBUG_VERSION) {
            this.startMessagingButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return IntroActivity.this.lambda$onCreate$1$IntroActivity(view);
                }
            });
        }
        BottomPagesView bottomPagesView = new BottomPagesView(this, this.viewPager, 4);
        this.bottomPages = bottomPagesView;
        frameLayout.addView(bottomPagesView, LayoutHelper.createFrame(44.0f, 5.0f, 81, 0.0f, 0.0f, 0.0f, 50.0f));
        TextView textView3 = new TextView(this);
        this.textView = textView3;
        textView3.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        frameLayout.addView(this.textView, LayoutHelper.createFrame(-2.0f, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                IntroActivity.this.lambda$onCreate$2$IntroActivity(view);
            }
        });
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout3 = new FrameLayout(this);
            setContentView(frameLayout3);
            View imageView = new ImageView(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            imageView.setBackgroundDrawable(drawable);
            frameLayout3.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout4 = new FrameLayout(this);
            frameLayout4.setBackgroundResource(R.drawable.btnshadow);
            frameLayout4.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout3.addView(frameLayout4, LayoutHelper.createFrame(498, 528, 17));
        } else {
            setRequestedOrientation(1);
            setContentView(scrollView);
        }
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        this.justCreated = true;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        AndroidUtilities.handleProxyIntent(this, getIntent());
    }

    public /* synthetic */ void lambda$onCreate$0$IntroActivity(View view) {
        if (!this.startPressed) {
            setNotFirstLaunch();
            this.startPressed = true;
            Intent intent2 = new Intent(this, LaunchActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
            this.destroyed = true;
            finish();
        }
    }

    public /* synthetic */ boolean lambda$onCreate$1$IntroActivity(View v) {
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
        return true;
    }

    public /* synthetic */ void lambda$onCreate$2$IntroActivity(View v) {
        if (!this.startPressed && this.localeInfo != null) {
            LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
            this.startPressed = true;
            Intent intent2 = new Intent(this, LaunchActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
            this.destroyed = true;
            finish();
        }
    }

    private void jumpIntro() {
        if (!MessagesController.getGlobalMainSettings().getBoolean("isFirstLaunch", true)) {
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.putExtra("fromIntro", true);
            startActivity(intent);
            this.destroyed = true;
            finish();
        }
    }

    private void setNotFirstLaunch() {
        SharedPreferences sp = MessagesController.getGlobalMainSettings();
        if (sp.getBoolean("isFirstLaunch", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).commit();
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo englishInfo = null;
        LocaleController.LocaleInfo systemInfo = null;
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        String arg = systemLang.contains("-") ? systemLang.split("-")[0] : systemLang;
        String alias = LocaleController.getLocaleAlias(arg);
        for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
            LocaleController.LocaleInfo info = LocaleController.getInstance().languages.get(a);
            if (info.shortName.equals("en")) {
                englishInfo = info;
            }
            if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || info.shortName.equals(alias)) {
                systemInfo = info;
            }
            if (englishInfo != null && systemInfo != null) {
                break;
            }
        }
        if (englishInfo != null && systemInfo != null && englishInfo != systemInfo) {
            TLRPC.TL_langpack_getStrings req = new TLRPC.TL_langpack_getStrings();
            if (systemInfo != currentLocaleInfo) {
                req.lang_code = systemInfo.getLangCode();
                this.localeInfo = systemInfo;
            } else {
                req.lang_code = englishInfo.getLangCode();
                this.localeInfo = englishInfo;
            }
            req.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(systemLang) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    IntroActivity.this.lambda$checkContinueText$4$IntroActivity(this.f$1, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$checkContinueText$4$IntroActivity(String systemLang, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (!vector.objects.isEmpty()) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(0);
                if (string instanceof TLRPC.TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new Runnable(string, systemLang) {
                        private final /* synthetic */ TLRPC.LangPackString f$1;
                        private final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            IntroActivity.this.lambda$null$3$IntroActivity(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$3$IntroActivity(TLRPC.LangPackString string, String systemLang) {
        if (!this.destroyed) {
            this.textView.setText(string.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang.toLowerCase()).commit();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = NotificationCenter.suggestedLangpack;
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout frameLayout = new FrameLayout(container.getContext());
            TextView headerTextView = new TextView(container.getContext());
            headerTextView.setTextColor(-14606047);
            headerTextView.setTextSize(1, 26.0f);
            headerTextView.setGravity(17);
            frameLayout.addView(headerTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 18.0f, 378.0f, 18.0f, 0.0f));
            TextView messageTextView = new TextView(container.getContext());
            messageTextView.setTextColor(-8355712);
            messageTextView.setTextSize(1, 15.0f);
            messageTextView.setGravity(17);
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 16.0f, 420.0f, 16.0f, 0.0f));
            container.addView(frameLayout, 0);
            headerTextView.setText(IntroActivity.this.titles[position]);
            messageTextView.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[position]));
            return frameLayout;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            IntroActivity.this.bottomPages.setCurrentPage(position);
            int unused = IntroActivity.this.currentViewPagerPage = position;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
