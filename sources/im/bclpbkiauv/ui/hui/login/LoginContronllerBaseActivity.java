package im.bclpbkiauv.ui.hui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.blankj.utilcode.util.ScreenUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.RegexUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.SlideView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.KeyboardChangeListener;
import im.bclpbkiauv.ui.utils.timer.RunningFlagCountDownTimer;
import java.util.Locale;

public abstract class LoginContronllerBaseActivity extends BaseFragment implements KeyboardChangeListener.KeyBoardListener {
    protected FrameLayout actionBarContainer;
    protected boolean canBack;
    protected FrameLayout contentContainer;
    protected int currentViewIndex;
    protected boolean keyboardIsShown;
    protected KeyboardChangeListener mKeyboardChangeListener;
    protected boolean newAccount;
    protected ThisView[] pages;
    protected FrameLayout rootView;

    /* access modifiers changed from: protected */
    public abstract void initPages(int i);

    /* access modifiers changed from: protected */
    public abstract void initView();

    public class ThisView_ViewBinding implements Unbinder {
        private ThisView target;
        private View view7f090096;

        public ThisView_ViewBinding(ThisView target2) {
            this(target2, target2);
        }

        public ThisView_ViewBinding(final ThisView target2, View source) {
            this.target = target2;
            target2.tvTitle = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'tvTitle'", MryTextView.class);
            View view = Utils.findRequiredView(source, R.id.btn, "field 'btn' and method 'onClick'");
            target2.btn = (MryRoundButton) Utils.castView(view, R.id.btn, "field 'btn'", MryRoundButton.class);
            this.view7f090096 = view;
            view.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
        }

        public void unbind() {
            ThisView target2 = this.target;
            if (target2 != null) {
                this.target = null;
                target2.tvTitle = null;
                target2.btn = null;
                this.view7f090096.setOnClickListener((View.OnClickListener) null);
                this.view7f090096 = null;
                return;
            }
            throw new IllegalStateException("Bindings already cleared.");
        }
    }

    public LoginContronllerBaseActivity() {
        this(UserConfig.selectedAccount, (Bundle) null);
    }

    public LoginContronllerBaseActivity(int account, Bundle args) {
        boolean z = true;
        this.canBack = true;
        args = args == null ? new Bundle() : args;
        args.putInt("account", account);
        args.putBoolean("newAccount", account == UserConfig.selectedAccount ? false : z);
        setArguments(args);
    }

    public boolean onFragmentCreate() {
        if (getArguments() != null) {
            this.currentAccount = getArguments().getInt("account");
            this.newAccount = getArguments().getBoolean("newAccount", false);
            boolean z = getArguments().getBoolean("canBack", true);
            this.canBack = z;
            if (z) {
                int hasCount = 0;
                for (int i = 0; i < 3; i++) {
                    if (!UserConfig.getInstance(i).isClientActivated()) {
                        hasCount++;
                    }
                }
                if (hasCount == 3) {
                    this.canBack = false;
                }
            }
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.rootView = new FrameLayout(context);
        ScrollView scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(40.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        scrollView.setFillViewport(true);
        this.fragmentView = this.rootView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.rootView.addView(scrollView, LayoutHelper.createScroll(-1, -1, 51));
        KeyboardChangeListener keyboardChangeListener = new KeyboardChangeListener((View) scrollView);
        this.mKeyboardChangeListener = keyboardChangeListener;
        keyboardChangeListener.setKeyBoardListener(this);
        FrameLayout frameLayout = new FrameLayout(context);
        this.contentContainer = frameLayout;
        scrollView.addView(frameLayout, LayoutHelper.createFrame(-1, -1, 51));
        initActionBar();
        initView();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    /* access modifiers changed from: protected */
    public void initActionBar() {
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackgroundColor(0);
        this.actionBar.setCastShadows(false);
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        this.actionBarContainer = frameLayout;
        frameLayout.setBackgroundColor(0);
        this.rootView.addView(this.actionBarContainer, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBarContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1 && LoginContronllerBaseActivity.this.onBackPressed()) {
                    LoginContronllerBaseActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setAcitonBar(int newPageIndex, ThisView thisView) {
        if (this.canBack) {
            this.actionBar.setBackButtonImage((thisView.needBackButton() || this.newAccount) ? R.mipmap.ic_back : 0);
        }
    }

    /* access modifiers changed from: protected */
    public final void toPage(int newPageIndex, boolean animated, Bundle params, boolean back) {
        initPages(newPageIndex);
        ThisView[] thisViewArr = this.pages;
        if (thisViewArr[newPageIndex] != null) {
            if (this.currentViewIndex != newPageIndex || thisViewArr[newPageIndex].getParent() == null) {
                if (this.pages[newPageIndex].getParent() == null) {
                    this.contentContainer.addView(this.pages[newPageIndex], LayoutHelper.createFrame(-1, -1, 51));
                }
                if (animated) {
                    ThisView[] thisViewArr2 = this.pages;
                    final ThisView outView = thisViewArr2[this.currentViewIndex];
                    final ThisView newView = thisViewArr2[newPageIndex];
                    newView.setClickable(true);
                    this.currentViewIndex = newPageIndex;
                    newView.setParams(params, false);
                    setParentActivityTitle(newView.getHeaderName());
                    setAcitonBar(newPageIndex, newView);
                    newView.onShow();
                    int i = AndroidUtilities.displaySize.x;
                    if (back) {
                        i = -i;
                    }
                    newView.setX((float) i);
                    newView.setVisibility(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            outView.clearFocus();
                            outView.setVisibility(8);
                            outView.setX(0.0f);
                            newView.onShowEnd();
                        }
                    });
                    Animator[] animatorArr = new Animator[2];
                    Property property = View.TRANSLATION_X;
                    float[] fArr = new float[1];
                    int i2 = AndroidUtilities.displaySize.x;
                    if (!back) {
                        i2 = -i2;
                    }
                    fArr[0] = (float) i2;
                    animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
                    animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(300);
                    animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    animatorSet.start();
                    return;
                }
                this.pages[this.currentViewIndex].setVisibility(8);
                this.currentViewIndex = newPageIndex;
                this.pages[newPageIndex].setParams(params, false);
                this.pages[newPageIndex].setVisibility(0);
                this.pages[newPageIndex].onShow();
                this.pages[newPageIndex].onShowEnd();
                setAcitonBar(newPageIndex, this.pages[newPageIndex]);
                setParentActivityTitle(this.pages[newPageIndex].getHeaderName());
                this.pages[newPageIndex].onShow();
                return;
            }
            this.pages[this.currentViewIndex].setParams(params, false);
        }
    }

    /* access modifiers changed from: protected */
    public void parseError(TLRPC.TL_error error, String extra) {
        if (error != null && !TextUtils.isEmpty(error.text)) {
            if (error.text.contains("PHONE_NUMBER_INVALID")) {
                needShowInvalidAlert(extra, false);
            } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                needShowAlert(LocaleController.getString(R.string.FloodWait));
            } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                needShowAlert(LocaleController.getString(R.string.PhoneNumberFlood));
            } else if (error.text.contains("PHONE_NUMBER_BANNED") || error.text.contains("ACCOUNT_RESTRICTED") || error.text.contains("ACCOUNT_BLOCKED")) {
                needShowInvalidAlert(extra, true);
            } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                needShowAlert(LocaleController.getString("InvalidCode", R.string.InvalidCode));
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                needShowAlert(LocaleController.getString("VerificationcodeExpired", R.string.VerificationcodeExpired));
            } else if (error.text.startsWith("FLOOD_WAIT")) {
                needShowAlert(LocaleController.getString(R.string.FloodWait));
            } else if (error.text.startsWith("CODE_VERIFY_LIMIT")) {
                needShowAlert(LocaleController.getString(R.string.CODE_VERIFY_LIMIT));
            } else if (error.text.startsWith("CODE_INVALID")) {
                needShowAlert(LocaleController.getString(R.string.InvalidCode));
            } else if (error.text.startsWith("PASSWORD_ERROR")) {
                needShowAlert(LocaleController.getString(R.string.LoginPwdError));
            } else if (error.text.startsWith("PHONE_NOT_SIGNUP") || error.text.startsWith("USERNAME_NOT_EXIST")) {
                needShowAlert(LocaleController.getString(R.string.UserNotRegistered));
            } else if (error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                needShowAlert(LocaleController.getString(R.string.UsernameAlreadyExists));
            } else if (error.text.startsWith("CURRENT_PWD_ERR")) {
                needShowAlert(LocaleController.getString(R.string.OldPwdError));
            } else if (error.text.startsWith("NOTEQUAL_TAG")) {
                needShowAlert(LocaleController.getString(R.string.LoginPwdError));
            } else if (error.text.startsWith("PASSWORD_INVALID")) {
                needShowAlert(LocaleController.getString(R.string.PasswordDoNotMatch));
            } else if (error.text.startsWith("PASSWORD_MANY")) {
                needShowAlert(LocaleController.getString(R.string.PWdErrorMany));
            } else if (error.text.startsWith("USERNAME_INVALID")) {
                needShowAlert(LocaleController.getString(R.string.UsernameInvalid));
            } else if (error.text.startsWith("USERNAME_OCCUPIED")) {
                needShowAlert(LocaleController.getString(R.string.UsernameInUse));
            } else if (error.text.contains("IPORDE_LIMIT")) {
                needShowAlert(LocaleController.getString("IpOrDeLimit", R.string.IpOrDeLimit));
            } else if (error.text.equals("INTERNAL")) {
                needShowAlert(LocaleController.getString("InternalError", R.string.InternalError));
            } else if (error.text.startsWith("GOOGLE_KEY_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.GOOGLEKEYERROR));
            } else if (error.text.equals("SINGLE_LIMIT")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.SINGLE_LIMIT));
            } else if (error.text.equals("WHITENULL")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.WHITENULL));
            } else if (error.text.equals("NOTENQUALIP")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.NOTENQUALIP));
            } else if (error.text.equals("OLD_VERSION_RESTRICT")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.OLD_VERSION_RESTRICT));
            } else if (error.text.equals("IP_BLOCKED")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.IP_BLOCKED));
            } else if (error.text.equals("FLOOD_WAIT_60")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.FLOOD_WAIT_60));
            } else if (error.text.equals("TYPE_CAST_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.TYPE_CAST_ERROR));
            } else if (error.text.equals("IP_SPEED_LIMIT")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.IP_SPEED_LIMIT));
            } else if (error.text.equals("RPC_FAIL")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.RPC_FAIL));
            } else if (error.text.equals("INTERNAL_ERROR") || error.text.equals("INTERNAL_ERRORO")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.INTERNAL_ERROR));
            } else if (error.text.equals("USERDELETE")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.USERDELETE));
            } else if (error.text.equals("NOT_ENGOGH")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.NOT_ENGOGH));
            } else if (error.text.equals("UNKNOWN_SOURCE_BLOCKED")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.UNKNOWN_SOURCE_BLOCKED));
            } else if (error.text.equals("NAME_TOO_LONG")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.NAME_TOO_LONG));
            } else if (error.text.equals("InvalidName")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.InvalidNames));
            } else if (error.text.equals("PROXYCODE_INVALID")) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.PROXYCODE_INVALID));
            } else {
                needShowAlert(LocaleController.getString(R.string.OperationFailedPleaseTryAgain) + "\n" + error.text);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void needShowInvalidAlert(String phoneNumber, boolean banned) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            if (banned) {
                builder.setMessage(LocaleController.getString(R.string.BannedPhoneNumber));
            } else {
                builder.setMessage(LocaleController.getString(R.string.InvalidPhoneNumber));
            }
            builder.setNeutralButton(LocaleController.getString(R.string.BotHelp), new DialogInterface.OnClickListener(banned, phoneNumber) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginContronllerBaseActivity.this.lambda$needShowInvalidAlert$0$LoginContronllerBaseActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$needShowInvalidAlert$0$LoginContronllerBaseActivity(boolean banned, String phoneNumber, DialogInterface dialog, int which) {
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
            Intent mailer = new Intent("android.intent.action.SEND");
            mailer.setType("message/rfc822");
            mailer.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
            if (banned) {
                mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut bclpbkiauv says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut bclpbkiauv says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
        } catch (Exception e) {
            needShowAlert(LocaleController.getString(R.string.NoMailInstalled));
        }
    }

    /* access modifiers changed from: protected */
    public void needShowAlert(String text) {
        if (text != null && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkPasswordRule(TextView et, boolean showErrorToast) {
        if (et == null || et.length() == 0) {
            return false;
        }
        String input = et.getText().toString().trim();
        if (input.length() >= 8 && input.length() <= 16 && RegexUtils.hasLetterAndNumber(input, false)) {
            return true;
        }
        if (showErrorToast) {
            ToastUtils.show((int) R.string.LoginPwdRule);
        }
        return false;
    }

    public void saveSelfArgs(Bundle args) {
        super.saveSelfArgs(args);
        args.putInt("currentIndex", this.currentViewIndex);
        args.putBundle("pageArgs", getArguments());
        Bundle bundle = new Bundle();
        if (this.pages != null) {
            int i = 0;
            while (true) {
                ThisView[] thisViewArr = this.pages;
                if (i < thisViewArr.length) {
                    ThisView t = thisViewArr[i];
                    if (t != null) {
                        t.saveStateParams(bundle);
                        args.putBundle("currentIndexB" + i, bundle);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        super.restoreSelfArgs(args);
        this.currentViewIndex = args.getInt("currentIndex");
        setArguments(args.getBundle("pageArgs"));
        if (this.pages != null) {
            int i = 0;
            while (true) {
                ThisView[] thisViewArr = this.pages;
                if (i < thisViewArr.length) {
                    ThisView t = thisViewArr[i];
                    t.restoreStateParams(args.getBundle("currentIndexB" + i));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public boolean onBackPressed() {
        ThisView[] thisViewArr = this.pages;
        if (thisViewArr != null) {
            int i = this.currentViewIndex;
            if (i == 0) {
                if (this.canBack) {
                    for (ThisView v : thisViewArr) {
                        if (v != null) {
                            v.onDestroyActivity();
                        }
                    }
                    return true;
                }
            } else if (i == 1) {
                thisViewArr[i].onBackPressed(true);
                toPage(0, true, (Bundle) null, true);
            } else if (i == 2) {
                thisViewArr[i].onBackPressed(true);
                toPage(1, true, (Bundle) null, true);
            }
        }
        return false;
    }

    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        int height;
        this.keyboardIsShown = isShow;
        View btn = this.pages[this.currentViewIndex].btn;
        if (!isFinishing() && btn != null && this.actionBar != null && isShow && (this.fragmentView instanceof ScrollView)) {
            int height2 = (AndroidUtilities.dp(10.0f) + keyboardHeight) - (ScreenUtils.getScreenHeight() - btn.getBottom());
            if (this.newAccount) {
                height = (this.actionBar != null ? this.actionBar.getHeight() : 0) + height2;
            } else {
                height = height2;
            }
            ((ScrollView) this.fragmentView).smoothScrollBy(0, height);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ThisView[] thisViewArr = this.pages;
        if (thisViewArr != null) {
            for (ThisView page : thisViewArr) {
                if (page != null) {
                    page.onDestroyActivity();
                }
            }
            this.pages = null;
        }
        KeyboardChangeListener keyboardChangeListener = this.mKeyboardChangeListener;
        if (keyboardChangeListener != null) {
            keyboardChangeListener.destroy();
            this.mKeyboardChangeListener = null;
        }
        this.rootView = null;
    }

    public static class ThisView extends SlideView {
        @BindView(2131296406)
        MryRoundButton btn;
        protected RunningFlagCountDownTimer countDownTimer;
        protected Bundle params;
        @BindView(2131297668)
        MryTextView tvTitle;
        private Unbinder unbinder;

        public ThisView(Context context) {
            super(context);
        }

        public void setParams(Bundle params2, boolean restore) {
            super.setParams(params2, restore);
            if (params2 != null) {
                this.params = params2;
            }
        }

        public Bundle getParams() {
            return this.params;
        }

        /* access modifiers changed from: protected */
        public void initView() {
            this.unbinder = ButterKnife.bind((Object) this, (View) this);
            this.tvTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.btn.setPrimaryRadiusAdjustBoundsFillStyle();
            loadSaveState();
        }

        public void onShow() {
            super.onShow();
        }

        /* access modifiers changed from: protected */
        public void onShowEnd() {
            setClickable(true);
        }

        /* access modifiers changed from: package-private */
        @OnClick({2131296406})
        public void onClick(View v) {
            if (v.getId() == R.id.btn) {
                checkEnterInfo(false, true, true);
            }
        }

        /* access modifiers changed from: protected */
        public boolean checkEnterInfo(boolean sendSmsCode, boolean showErrorToast, boolean toNextStep) {
            return true;
        }

        public String getHeaderName() {
            return this.tvTitle.getText().toString().trim();
        }

        public boolean needBackButton() {
            return true;
        }

        public void saveStateParams(Bundle bundle) {
            super.saveStateParams(bundle);
        }

        /* access modifiers changed from: protected */
        public SharedPreferences getSp() {
            return ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0);
        }

        /* access modifiers changed from: protected */
        public void loadSaveState() {
            long mills = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getLong("countDownTimer", 0);
            if (mills > 0) {
                startCountDownTimer(mills);
            }
        }

        /* access modifiers changed from: protected */
        public void saveLastSendSmsTime() {
            SharedPreferences.Editor editor = getSp().edit();
            editor.putLong("last_time", System.currentTimeMillis());
            editor.apply();
        }

        /* access modifiers changed from: protected */
        public long getLastSendSmsTime() {
            return getSp().getLong("last_time", 0);
        }

        /* access modifiers changed from: protected */
        public void startCountDownTimer(long countDownMills) {
            if (this.countDownTimer == null) {
                AnonymousClass1 r1 = new RunningFlagCountDownTimer(countDownMills, 1000) {
                    public void onTick(long millisUntilFinished) {
                        super.onTick(millisUntilFinished);
                        ThisView.this.onTimerTick(millisUntilFinished);
                    }

                    public void onFinish() {
                        super.onFinish();
                        ThisView.this.onTimerFinish();
                    }
                };
                this.countDownTimer = r1;
                r1.startInternal();
            }
        }

        /* access modifiers changed from: protected */
        public void onTimerTick(long millisUntilFinished) {
            MryRoundButton mryRoundButton = this.btn;
            if (mryRoundButton != null) {
                mryRoundButton.setText(LocaleController.formatString("ResendPhoneCodeCountDown2", R.string.ResendPhoneCodeCountDown2, Long.valueOf(millisUntilFinished / 1000)));
                this.btn.setEnabled(false);
            }
        }

        /* access modifiers changed from: protected */
        public void onTimerFinish() {
            MryRoundButton mryRoundButton = this.btn;
            if (mryRoundButton != null) {
                mryRoundButton.setText(LocaleController.getString(R.string.SendVerifyCode));
                this.btn.setEnabled(true);
            }
        }

        /* access modifiers changed from: protected */
        public void stopTimer() {
            try {
                if (this.countDownTimer != null && this.countDownTimer.isRunning()) {
                    this.countDownTimer.cancelInternal();
                    this.countDownTimer = null;
                }
            } catch (Exception e) {
            }
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            stopTimer();
            Unbinder unbinder2 = this.unbinder;
            if (unbinder2 != null) {
                try {
                    unbinder2.unbind();
                } catch (Exception e) {
                }
                this.unbinder = null;
            }
            this.params = null;
        }
    }
}
