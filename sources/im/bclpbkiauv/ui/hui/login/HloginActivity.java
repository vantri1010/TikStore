package im.bclpbkiauv.ui.hui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.blankj.utilcode.constant.TimeConstants;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SRPHelper;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.ExternalActionActivity;
import im.bclpbkiauv.ui.IndexActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.TwoStepVerificationActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.HintEditText;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.SlideView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.login.HloginActivity;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import kotlin.text.Typography;
import org.slf4j.Marker;

@Deprecated
public class HloginActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    private boolean checkShowPermissions = true;
    /* access modifiers changed from: private */
    public TLRPC.TL_help_termsOfService currentTermsOfService;
    /* access modifiers changed from: private */
    public int currentViewNum;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public ContextProgressView doneProgressView;
    /* access modifiers changed from: private */
    public boolean newAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList<>();
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems = new ArrayList<>();
    private int progressRequestId;
    /* access modifiers changed from: private */
    public int scrollHeight;
    /* access modifiers changed from: private */
    public boolean syncContacts = true;
    /* access modifiers changed from: private */
    public SlideView[] views = new SlideView[9];

    private class ProgressView extends View {
        private Paint paint = new Paint();
        private Paint paint2 = new Paint();
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor(Theme.key_login_progressInner));
            this.paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int start = (int) (((float) getMeasuredWidth()) * this.progress);
            canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public HloginActivity() {
    }

    public HloginActivity(int account) {
        this.currentAccount = account;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a < slideViewArr.length) {
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            } else {
                return;
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    if (HloginActivity.this.doneProgressView.getTag() == null) {
                        HloginActivity.this.views[HloginActivity.this.currentViewNum].onNextPressed();
                    } else if (HloginActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) HloginActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
                        builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), (DialogInterface.OnClickListener) null);
                        builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                HloginActivity.AnonymousClass1.this.lambda$onItemClick$0$HloginActivity$1(dialogInterface, i);
                            }
                        });
                        HloginActivity.this.showDialog(builder.create());
                    }
                } else if (id == -1 && HloginActivity.this.onBackPressed()) {
                    HloginActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$HloginActivity$1(DialogInterface dialogInterface, int i) {
                HloginActivity.this.views[HloginActivity.this.currentViewNum].onCancelPressed();
                HloginActivity.this.needHideProgress(true);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.actionBar.setBackButtonDrawable(DrawableUtils.tintDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_login_back).mutate(), Theme.getColor(Theme.key_actionBarDefaultIcon)));
        this.actionBar.setCastShadows(false);
        this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.doneProgressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.doneProgressView.setScaleX(0.1f);
        this.doneProgressView.setScaleY(0.1f);
        this.doneProgressView.setVisibility(4);
        this.doneItem.addView(this.doneProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.doneItem.setContentDescription(LocaleController.getString("Done", R.string.Done));
        ScrollView scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (HloginActivity.this.currentViewNum == 1 || HloginActivity.this.currentViewNum == 2 || HloginActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = HloginActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        scrollView.setFillViewport(true);
        this.fragmentView = scrollView;
        FrameLayout frameLayout = new FrameLayout(context2);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(this, context2);
        this.views[1] = new LoginActivitySmsView(this, context2, 1);
        this.views[2] = new LoginActivitySmsView(this, context2, 2);
        this.views[3] = new LoginActivitySmsView(this, context2, 3);
        this.views[4] = new LoginActivitySmsView(this, context2, 4);
        this.views[5] = new LoginActivityRegisterView(this, context2);
        this.views[6] = new LoginActivityPasswordView(this, context2);
        this.views[7] = new LoginActivityRecoverView(this, context2);
        this.views[8] = new LoginActivityResetWaitView(this, context2);
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                break;
            }
            slideViewArr[a].setVisibility(a == 0 ? 0 : 8);
            SlideView slideView = this.views[a];
            float f = 18.0f;
            float f2 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            }
            frameLayout.addView(slideView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, f2, 30.0f, f, 0.0f));
            a++;
        }
        Bundle savedInstanceState = loadCurrentState();
        if (savedInstanceState != null) {
            this.currentViewNum = savedInstanceState.getInt("currentViewNum", 0);
            this.syncContacts = savedInstanceState.getInt("syncContacts", 1) == 1;
            int i = this.currentViewNum;
            if (i >= 1 && i <= 4) {
                int time = savedInstanceState.getInt("open");
                if (time != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    this.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            } else if (this.currentViewNum == 6) {
                LoginActivityPasswordView view = (LoginActivityPasswordView) this.views[6];
                if (view.passwordType == 0 || view.current_salt1 == null || view.current_salt2 == null) {
                    this.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            }
        }
        int a2 = 0;
        while (true) {
            SlideView[] slideViewArr2 = this.views;
            if (a2 >= slideViewArr2.length) {
                return this.fragmentView;
            }
            if (savedInstanceState != null) {
                if (a2 < 1 || a2 > 4) {
                    this.views[a2].restoreStateParams(savedInstanceState);
                } else if (a2 == this.currentViewNum) {
                    slideViewArr2[a2].restoreStateParams(savedInstanceState);
                }
            }
            if (this.currentViewNum == a2) {
                this.views[a2].setVisibility(0);
                this.views[a2].onShow();
                if (a2 == 3 || a2 == 8) {
                    this.doneItem.setVisibility(8);
                }
            } else {
                this.views[a2].setVisibility(8);
            }
            a2++;
        }
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    public void onResume() {
        int time;
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        try {
            if (this.currentViewNum >= 1 && this.currentViewNum <= 4 && (this.views[this.currentViewNum] instanceof LoginActivitySmsView) && (time = ((LoginActivitySmsView) this.views[this.currentViewNum]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                this.views[this.currentViewNum].onBackPressed(true);
                setPage(0, false, (Bundle) null, true);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            int i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed();
            }
        } else if (requestCode == 7) {
            this.checkShowPermissions = false;
            int i2 = this.currentViewNum;
            if (i2 == 0) {
                ((PhoneView) this.views[i2]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
        if (this.newAccount) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, ?> entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String[] args = key.split("_\\|_");
                if (args.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer) value).intValue());
                    }
                } else if (args.length == 2) {
                    Bundle inner = bundle.getBundle(args[0]);
                    if (inner == null) {
                        inner = new Bundle();
                        bundle.putBundle(args[0], inner);
                    }
                    if (value instanceof String) {
                        inner.putString(args[1], (String) value);
                    } else if (value instanceof Integer) {
                        inner.putInt(args[1], ((Integer) value).intValue());
                    }
                }
            }
            return bundle;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    private void clearCurrentState() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        editor.clear();
        editor.commit();
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String prefix) {
        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, ((Integer) obj).intValue());
                } else {
                    editor.putInt(key, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
            } catch (Exception e) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
            } catch (Exception e2) {
            }
        }
    }

    public boolean onBackPressed() {
        int i = this.currentViewNum;
        if (i == 0) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a < slideViewArr.length) {
                    if (slideViewArr[a] != null) {
                        slideViewArr[a].onDestroyActivity();
                    }
                    a++;
                } else {
                    clearCurrentState();
                    return true;
                }
            }
        } else {
            if (i == 6) {
                this.views[i].onBackPressed(true);
                setPage(0, true, (Bundle) null, true);
            } else if (i == 7 || i == 8) {
                this.views[this.currentViewNum].onBackPressed(true);
                setPage(6, true, (Bundle) null, true);
            } else if (i < 1 || i > 4) {
                int i2 = this.currentViewNum;
                if (i2 == 5) {
                    ((LoginActivityRegisterView) this.views[i2]).wrongNumber.callOnClick();
                }
            } else if (this.views[i].onBackPressed(false)) {
                setPage(0, true, (Bundle) null, true);
            }
            return false;
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        LoginActivityRegisterView registerView = (LoginActivityRegisterView) this.views[5];
        if (registerView != null) {
            registerView.imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* access modifiers changed from: private */
    public void needShowAlert(String title, String text) {
        if (text != null && getParentActivity() != null) {
            XDialog.Builder builder = new XDialog.Builder(getParentActivity());
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void needShowInvalidAlert(String phoneNumber, boolean banned) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (banned) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", R.string.BannedPhoneNumber));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", R.string.BotHelp), new DialogInterface.OnClickListener(banned, phoneNumber) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    HloginActivity.this.lambda$needShowInvalidAlert$0$HloginActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$needShowInvalidAlert$0$HloginActivity(boolean banned, String phoneNumber, DialogInterface dialog, int which) {
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
            Intent mailer = new Intent("android.intent.action.SEND");
            mailer.setType("message/rfc822");
            mailer.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
            if (banned) {
                mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut App says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut App says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
        } catch (Exception e) {
            needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
        }
    }

    private void showEditDoneProgress(final boolean show) {
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        if (show) {
            this.doneProgressView.setTag(1);
            this.doneProgressView.setVisibility(0);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.doneProgressView.setTag((Object) null);
            this.doneItem.getContentView().setVisibility(0);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (HloginActivity.this.doneItemAnimation != null && HloginActivity.this.doneItemAnimation.equals(animation)) {
                    if (!show) {
                        HloginActivity.this.doneProgressView.setVisibility(4);
                    } else {
                        HloginActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (HloginActivity.this.doneItemAnimation != null && HloginActivity.this.doneItemAnimation.equals(animation)) {
                    AnimatorSet unused = HloginActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int reqiestId) {
        this.progressRequestId = reqiestId;
        showEditDoneProgress(true);
    }

    public void needHideProgress(boolean cancel) {
        if (this.progressRequestId != 0) {
            if (cancel) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        showEditDoneProgress(false);
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (page == 3 || page == 8) {
            this.doneItem.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.doneItem.setVisibility(0);
        }
        int i = this.currentViewNum;
        if (i == page) {
            this.views[i].setParams(params, false);
        } else if (animated) {
            SlideView[] slideViewArr = this.views;
            final SlideView outView = slideViewArr[i];
            SlideView newView = slideViewArr[page];
            this.currentViewNum = page;
            newView.setParams(params, false);
            setParentActivityTitle(newView.getHeaderName());
            newView.onShow();
            int i2 = AndroidUtilities.displaySize.x;
            if (back) {
                i2 = -i2;
            }
            newView.setX((float) i2);
            newView.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            int i3 = AndroidUtilities.displaySize.x;
            if (!back) {
                i3 = -i3;
            }
            fArr[0] = (float) i3;
            animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
        } else {
            this.actionBar.setBackButtonImage((this.views[page].needBackButton() || this.newAccount) ? R.mipmap.ic_back : 0);
            this.views[this.currentViewNum].setVisibility(8);
            this.currentViewNum = page;
            this.views[page].setParams(params, false);
            this.views[page].setVisibility(0);
            setParentActivityTitle(this.views[page].getHeaderName());
            this.views[page].onShow();
        }
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts ? 1 : 0);
            for (int a = 0; a <= this.currentViewNum; a++) {
                SlideView v = this.views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            editor.clear();
            putBundleToEditor(bundle, editor, (String) null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void needFinishActivity() {
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, true);
                finishFragment();
                return;
            }
            presentFragment(new IndexActivity(), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(res.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkProxyInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        needFinishActivity();
    }

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeApp) {
            params.putInt("type", 1);
            params.putInt("length", res.type.length);
            setPage(1, true, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, true, params, false);
        }
    }

    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener {
        private CheckBoxCell checkBoxCell;
        /* access modifiers changed from: private */
        public EditTextBoldCursor codeField;
        /* access modifiers changed from: private */
        public HashMap<String, String> codesMap = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<String> countriesArray = new ArrayList<>();
        private HashMap<String, String> countriesMap = new HashMap<>();
        /* access modifiers changed from: private */
        public TextView countryButton;
        /* access modifiers changed from: private */
        public int countryState = 0;
        /* access modifiers changed from: private */
        public boolean ignoreOnPhoneChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreSelection = false;
        private boolean nextPressed = false;
        /* access modifiers changed from: private */
        public HintEditText phoneField;
        /* access modifiers changed from: private */
        public HashMap<String, String> phoneFormatMap = new HashMap<>();
        /* access modifiers changed from: private */
        public TextView textView;
        /* access modifiers changed from: private */
        public TextView textView2;
        final /* synthetic */ HloginActivity this$0;
        /* access modifiers changed from: private */
        public View view;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(im.bclpbkiauv.ui.hui.login.HloginActivity r44, android.content.Context r45) {
            /*
                r43 = this;
                r1 = r43
                r2 = r44
                r3 = r45
                r1.this$0 = r2
                r1.<init>(r3)
                r0 = 0
                r1.countryState = r0
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r1.countriesArray = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.countriesMap = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.codesMap = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.phoneFormatMap = r4
                r1.ignoreSelection = r0
                r1.ignoreOnTextChange = r0
                r1.ignoreOnPhoneChange = r0
                r1.nextPressed = r0
                r4 = 1
                r1.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r3)
                r6 = -1
                r7 = -2
                r8 = 0
                r9 = 1109917696(0x42280000, float:42.0)
                r10 = 0
                r11 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r6, (int) r7, (float) r8, (float) r9, (float) r10, (float) r11)
                r1.addView(r5, r6)
                java.lang.String r6 = "Login"
                r7 = 2131691864(0x7f0f0958, float:1.9012812E38)
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r7)
                r5.setText(r6)
                r6 = 17
                r5.setGravity(r6)
                java.lang.String r7 = "windowBackgroundWhiteBlackText"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r5.setTextColor(r8)
                r8 = 1103101952(0x41c00000, float:24.0)
                r5.setTextSize(r8)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r3)
                r10 = -1
                r11 = -2
                r12 = 0
                r13 = 1095237632(0x41480000, float:12.5)
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (float) r12, (float) r13, (float) r14, (float) r15)
                r1.addView(r9, r10)
                r10 = 2131694034(0x7f0f11d2, float:1.9017213E38)
                java.lang.String r11 = "StartText"
                java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
                r9.setText(r12)
                r9.setGravity(r6)
                java.lang.String r12 = "windowBackgroundWhiteGrayText"
                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
                r9.setTextColor(r12)
                r12 = 1096810496(0x41600000, float:14.0)
                r9.setTextSize(r12)
                android.view.View r13 = new android.view.View
                r13.<init>(r3)
                r1.view = r13
                r14 = 1098907648(0x41800000, float:16.0)
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                r13.setPadding(r15, r0, r8, r0)
                android.view.View r8 = r1.view
                java.lang.String r13 = "windowBackgroundWhiteGrayLine"
                int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
                r8.setBackgroundColor(r15)
                android.view.View r8 = r1.view
                r17 = -1082130432(0xffffffffbf800000, float:-1.0)
                r18 = 1056964608(0x3f000000, float:0.5)
                r19 = 0
                r20 = 1110441984(0x42300000, float:44.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r17, (float) r18, (float) r19, (float) r20, (float) r21, (float) r22)
                r1.addView(r8, r15)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r3)
                r1.countryButton = r8
                r15 = 1099956224(0x41900000, float:18.0)
                r8.setTextSize(r4, r15)
                android.widget.TextView r8 = r1.countryButton
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                r8.setPadding(r6, r0, r12, r0)
                android.widget.TextView r6 = r1.countryButton
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r6.setTextColor(r8)
                android.widget.TextView r6 = r1.countryButton
                r6.setMaxLines(r4)
                android.widget.TextView r6 = r1.countryButton
                r6.setSingleLine(r4)
                android.widget.TextView r6 = r1.countryButton
                android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r8)
                android.widget.TextView r6 = r1.countryButton
                r8 = 19
                r6.setGravity(r8)
                android.widget.TextView r6 = r1.countryButton
                r12 = 2131231608(0x7f080378, float:1.8079302E38)
                r6.setBackgroundResource(r12)
                android.widget.TextView r6 = r1.countryButton
                r19 = -1
                r20 = 48
                r23 = 0
                r24 = 0
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r19, (int) r20, (float) r21, (float) r22, (float) r23, (float) r24)
                r1.addView(r6, r12)
                android.widget.TextView r6 = r1.countryButton
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$AQqQBZF1mxStEFLFZGWDnMp1bQ8 r12 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$AQqQBZF1mxStEFLFZGWDnMp1bQ8
                r12.<init>()
                r6.setOnClickListener(r12)
                android.view.View r6 = new android.view.View
                r6.<init>(r3)
                r1.view = r6
                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
                r6.setBackgroundColor(r12)
                android.view.View r6 = r1.view
                r12 = 1056964608(0x3f000000, float:0.5)
                r14 = -1082130432(0xffffffffbf800000, float:-1.0)
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r14, (float) r12)
                r1.addView(r6, r10)
                android.widget.LinearLayout r6 = new android.widget.LinearLayout
                r6.<init>(r3)
                r6.setOrientation(r0)
                r21 = -1
                r22 = 58
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r21, (int) r22, (float) r23, (float) r24, (float) r25, (float) r26)
                r1.addView(r6, r10)
                android.widget.ImageView r10 = new android.widget.ImageView
                r10.<init>(r3)
                androidx.fragment.app.FragmentActivity r21 = r44.getParentActivity()
                android.content.res.Resources r12 = r21.getResources()
                r14 = 2131558712(0x7f0d0138, float:1.8742748E38)
                android.graphics.drawable.Drawable r12 = r12.getDrawable(r14)
                android.graphics.drawable.Drawable r12 = r12.mutate()
                java.lang.String r14 = "actionBarDefaultIcon"
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                android.graphics.drawable.Drawable r12 = im.bclpbkiauv.messenger.utils.DrawableUtils.tintDrawable(r12, r14)
                r23 = -2
                r24 = -1
                r25 = 1084227584(0x40a00000, float:5.0)
                r27 = 0
                r28 = 0
                android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (float) r25, (float) r26, (float) r27, (float) r28)
                r6.addView(r10, r14)
                r10.setImageDrawable(r12)
                android.widget.TextView r14 = new android.widget.TextView
                r14.<init>(r3)
                r1.textView = r14
                java.lang.String r8 = "+"
                r14.setText(r8)
                android.widget.TextView r8 = r1.textView
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r8.setTextColor(r14)
                android.widget.TextView r8 = r1.textView
                r8.setTextSize(r4, r15)
                android.widget.TextView r8 = r1.textView
                r14 = 16
                r8.setGravity(r14)
                android.widget.TextView r8 = r1.textView
                r24 = -2
                r25 = -1
                r26 = 1096810496(0x41600000, float:14.0)
                r29 = 0
                android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r24, (int) r25, (float) r26, (float) r27, (float) r28, (float) r29)
                r6.addView(r8, r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                r8.<init>(r3)
                r1.codeField = r8
                r14 = 3
                r8.setInputType(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r8.setTextColor(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                r14 = 0
                r8.setBackground(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r8.setCursorColor(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                r14 = 1101004800(0x41a00000, float:20.0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                r8.setCursorSize(r4)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r8 = 1069547520(0x3fc00000, float:1.5)
                r4.setCursorWidth(r8)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r27 = 1082130432(0x40800000, float:4.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r27)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r27)
                r4.setPadding(r8, r0, r14, r0)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r8 = 1
                r4.setTextSize(r8, r15)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r4.setMaxLines(r8)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r14 = 19
                r4.setGravity(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r1.codeField
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r4.setImeOptions(r14)
                android.text.InputFilter[] r4 = new android.text.InputFilter[r8]
                android.text.InputFilter$LengthFilter r8 = new android.text.InputFilter$LengthFilter
                r14 = 5
                r8.<init>(r14)
                r4[r0] = r8
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                r8.setFilters(r4)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                r30 = 46
                r31 = -1
                r32 = 0
                r33 = 0
                r34 = 0
                r35 = 0
                android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r30, (int) r31, (float) r32, (float) r33, (float) r34, (float) r35)
                r6.addView(r8, r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$1 r14 = new im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$1
                r14.<init>(r2)
                r8.addTextChangedListener(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r1.codeField
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$wNDSEm0f3x3En7JI8vg9Slv5NF8 r14 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$wNDSEm0f3x3En7JI8vg9Slv5NF8
                r14.<init>()
                r8.setOnEditorActionListener(r14)
                android.view.View r8 = new android.view.View
                r8.<init>(r3)
                r1.view = r8
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
                r8.setBackgroundColor(r14)
                android.view.View r8 = r1.view
                r36 = 1056964608(0x3f000000, float:0.5)
                r37 = 1107296256(0x42000000, float:32.0)
                r38 = 16
                r39 = 1098907648(0x41800000, float:16.0)
                r40 = 0
                r41 = 1098907648(0x41800000, float:16.0)
                r42 = 0
                android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r36, (float) r37, (int) r38, (float) r39, (float) r40, (float) r41, (float) r42)
                r6.addView(r8, r14)
                im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$2 r8 = new im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$2
                r8.<init>(r3, r2)
                r1.phoneField = r8
                r14 = 3
                r8.setInputType(r14)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r8.setTextColor(r14)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                java.lang.String r14 = "windowBackgroundWhiteHintText"
                int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                r8.setHintTextColor(r14)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                r14 = 0
                r8.setBackground(r14)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                r8.setPadding(r0, r0, r0, r0)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r8.setCursorColor(r7)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = 1101004800(0x41a00000, float:20.0)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                r7.setCursorSize(r14)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = 1069547520(0x3fc00000, float:1.5)
                r7.setCursorWidth(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = 1
                r7.setTextSize(r8, r15)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r7.setMaxLines(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = 19
                r7.setGravity(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = 268435461(0x10000005, float:2.5243564E-29)
                r7.setImeOptions(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                r8 = -1
                r14 = -1082130432(0xffffffffbf800000, float:-1.0)
                android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r14)
                r6.addView(r7, r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$3 r8 = new im.bclpbkiauv.ui.hui.login.HloginActivity$PhoneView$3
                r8.<init>(r2)
                r7.addTextChangedListener(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$8dg6eN7YRcpnB9EtFV_7h5I6Jas r8 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$8dg6eN7YRcpnB9EtFV_7h5I6Jas
                r8.<init>()
                r7.setOnEditorActionListener(r8)
                im.bclpbkiauv.ui.components.HintEditText r7 = r1.phoneField
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$Vn9fyesuLl7paz9A4E8gLkeWoL4 r8 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$Vn9fyesuLl7paz9A4E8gLkeWoL4
                r8.<init>()
                r7.setOnKeyListener(r8)
                android.view.View r7 = new android.view.View
                r7.<init>(r3)
                r1.view = r7
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
                r7.setBackgroundColor(r8)
                android.view.View r7 = r1.view
                r8 = 1056964608(0x3f000000, float:0.5)
                r13 = -1082130432(0xffffffffbf800000, float:-1.0)
                android.widget.LinearLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r13, (float) r8)
                r1.addView(r7, r8)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r3)
                r1.textView2 = r7
                r8 = 2131694034(0x7f0f11d2, float:1.9017213E38)
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r8)
                r7.setText(r8)
                android.widget.TextView r7 = r1.textView2
                java.lang.String r8 = "windowBackgroundWhiteGrayText6"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r7.setTextColor(r8)
                android.widget.TextView r7 = r1.textView2
                r8 = 1096810496(0x41600000, float:14.0)
                r11 = 1
                r7.setTextSize(r11, r8)
                android.widget.TextView r7 = r1.textView2
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x034f
                r14 = 5
                goto L_0x0350
            L_0x034f:
                r14 = 3
            L_0x0350:
                r7.setGravity(r14)
                android.widget.TextView r7 = r1.textView2
                r8 = 1073741824(0x40000000, float:2.0)
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r13 = 1065353216(0x3f800000, float:1.0)
                r7.setLineSpacing(r11, r13)
                android.widget.TextView r7 = r1.textView2
                r36 = -2
                r37 = -2
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x036e
                r38 = 5
                goto L_0x0370
            L_0x036e:
                r38 = 3
            L_0x0370:
                r39 = 0
                r40 = 28
                r41 = 0
                r42 = 10
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r36, (int) r37, (int) r38, (int) r39, (int) r40, (int) r41, (int) r42)
                r1.addView(r7, r11)
                android.widget.TextView r7 = r1.textView2
                r11 = 8
                r7.setVisibility(r11)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r3)
                r11 = 2131692179(0x7f0f0a93, float:1.901345E38)
                java.lang.String r13 = "Next"
                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r11)
                java.lang.String r11 = r11.toUpperCase()
                r7.setText(r11)
                r11 = 17
                r7.setGravity(r11)
                r11 = -1
                r7.setTextColor(r11)
                r11 = 1098907648(0x41800000, float:16.0)
                r13 = 1
                r7.setTextSize(r13, r11)
                r11 = 1103101952(0x41c00000, float:24.0)
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                float r11 = (float) r11
                java.lang.String r13 = "#FF268CFF"
                int r13 = android.graphics.Color.parseColor(r13)
                java.lang.String r14 = "#FF1E69BD"
                int r14 = android.graphics.Color.parseColor(r14)
                android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorRoundRectDrawable(r11, r13, r14)
                r7.setBackground(r11)
                int r11 = android.os.Build.VERSION.SDK_INT
                r13 = 21
                r14 = 2
                if (r11 < r13) goto L_0x0423
                android.animation.StateListAnimator r11 = new android.animation.StateListAnimator
                r11.<init>()
                r13 = 1
                int[] r15 = new int[r13]
                r16 = 16842919(0x10100a7, float:2.3694026E-38)
                r15[r0] = r16
                float[] r13 = new float[r14]
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r14 = (float) r14
                r13[r0] = r14
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r27)
                float r14 = (float) r14
                r17 = 1
                r13[r17] = r14
                java.lang.String r14 = "translationZ"
                android.animation.ObjectAnimator r13 = android.animation.ObjectAnimator.ofFloat(r7, r14, r13)
                r14 = r9
                r8 = 200(0xc8, double:9.9E-322)
                android.animation.ObjectAnimator r8 = r13.setDuration(r8)
                r11.addState(r15, r8)
                int[] r8 = new int[r0]
                r9 = 2
                float[] r13 = new float[r9]
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r27)
                float r9 = (float) r9
                r13[r0] = r9
                r9 = 1073741824(0x40000000, float:2.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                float r9 = (float) r9
                r15 = 1
                r13[r15] = r9
                java.lang.String r9 = "translationZ"
                android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r7, r9, r13)
                r0 = 200(0xc8, double:9.9E-322)
                android.animation.ObjectAnimator r0 = r9.setDuration(r0)
                r11.addState(r8, r0)
                r7.setStateListAnimator(r11)
                goto L_0x0424
            L_0x0423:
                r14 = r9
            L_0x0424:
                r0 = 1101004800(0x41a00000, float:20.0)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                r8 = 1092616192(0x41200000, float:10.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                r9 = 1092616192(0x41200000, float:10.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r7.setPadding(r1, r8, r0, r9)
                r17 = -1
                r18 = -2
                r19 = 17
                r20 = 32
                r21 = 56
                r22 = 32
                r23 = 0
                android.widget.LinearLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r1 = r43
                r1.addView(r7, r0)
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$IkVoEv2vGyt9wVV1EMaKNv89POY r0 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$IkVoEv2vGyt9wVV1EMaKNv89POY
                r0.<init>()
                r7.setOnClickListener(r0)
                boolean r0 = r44.newAccount
                if (r0 == 0) goto L_0x049e
                im.bclpbkiauv.ui.cells.CheckBoxCell r0 = new im.bclpbkiauv.ui.cells.CheckBoxCell
                r8 = 2
                r0.<init>(r3, r8)
                r1.checkBoxCell = r0
                r8 = 2131694107(0x7f0f121b, float:1.9017361E38)
                java.lang.String r9 = "SyncContacts"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                boolean r9 = r44.syncContacts
                java.lang.String r11 = ""
                r13 = 0
                r0.setText(r8, r11, r9, r13)
                im.bclpbkiauv.ui.cells.CheckBoxCell r0 = r1.checkBoxCell
                r17 = -2
                r18 = -1
                r19 = 51
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r1.addView(r0, r8)
                im.bclpbkiauv.ui.cells.CheckBoxCell r0 = r1.checkBoxCell
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$pqaWQ0l-0usRbPHnH0usoEPe0BQ r8 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$pqaWQ0l-0usRbPHnH0usoEPe0BQ
                r8.<init>()
                r0.setOnClickListener(r8)
            L_0x049e:
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r8 = r0
                java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0506 }
                java.io.InputStreamReader r9 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0506 }
                android.content.res.Resources r11 = r43.getResources()     // Catch:{ Exception -> 0x0506 }
                android.content.res.AssetManager r11 = r11.getAssets()     // Catch:{ Exception -> 0x0506 }
                java.lang.String r15 = "countries.txt"
                java.io.InputStream r11 = r11.open(r15)     // Catch:{ Exception -> 0x0506 }
                r9.<init>(r11)     // Catch:{ Exception -> 0x0506 }
                r0.<init>(r9)     // Catch:{ Exception -> 0x0506 }
            L_0x04bc:
                java.lang.String r9 = r0.readLine()     // Catch:{ Exception -> 0x0506 }
                r11 = r9
                if (r9 == 0) goto L_0x0502
                java.lang.String r9 = ";"
                java.lang.String[] r9 = r11.split(r9)     // Catch:{ Exception -> 0x0506 }
                java.util.ArrayList<java.lang.String> r15 = r1.countriesArray     // Catch:{ Exception -> 0x0506 }
                r16 = 2
                r13 = r9[r16]     // Catch:{ Exception -> 0x0506 }
                r2 = 0
                r15.add(r2, r13)     // Catch:{ Exception -> 0x0506 }
                java.util.HashMap<java.lang.String, java.lang.String> r13 = r1.countriesMap     // Catch:{ Exception -> 0x0506 }
                r15 = r9[r16]     // Catch:{ Exception -> 0x0506 }
                r3 = r9[r2]     // Catch:{ Exception -> 0x0506 }
                r13.put(r15, r3)     // Catch:{ Exception -> 0x0506 }
                java.util.HashMap<java.lang.String, java.lang.String> r3 = r1.codesMap     // Catch:{ Exception -> 0x0506 }
                r15 = r9[r2]     // Catch:{ Exception -> 0x0506 }
                r2 = 2
                r13 = r9[r2]     // Catch:{ Exception -> 0x0506 }
                r3.put(r15, r13)     // Catch:{ Exception -> 0x0506 }
                int r2 = r9.length     // Catch:{ Exception -> 0x0506 }
                r3 = 3
                if (r2 <= r3) goto L_0x04f4
                java.util.HashMap<java.lang.String, java.lang.String> r2 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0506 }
                r13 = 0
                r15 = r9[r13]     // Catch:{ Exception -> 0x0506 }
                r13 = r9[r3]     // Catch:{ Exception -> 0x0506 }
                r2.put(r15, r13)     // Catch:{ Exception -> 0x0506 }
            L_0x04f4:
                r2 = 1
                r13 = r9[r2]     // Catch:{ Exception -> 0x0506 }
                r2 = 2
                r15 = r9[r2]     // Catch:{ Exception -> 0x0506 }
                r8.put(r13, r15)     // Catch:{ Exception -> 0x0506 }
                r2 = r44
                r3 = r45
                goto L_0x04bc
            L_0x0502:
                r0.close()     // Catch:{ Exception -> 0x0506 }
                goto L_0x050a
            L_0x0506:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x050a:
                java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
                im.bclpbkiauv.ui.hui.login.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r2 = im.bclpbkiauv.ui.hui.login.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
                java.util.Collections.sort(r0, r2)
                r2 = 0
                android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0520 }
                java.lang.String r3 = "phone"
                java.lang.Object r0 = r0.getSystemService(r3)     // Catch:{ Exception -> 0x0520 }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0520 }
                if (r0 == 0) goto L_0x051f
                r2 = 0
            L_0x051f:
                goto L_0x0524
            L_0x0520:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0524:
                if (r2 == 0) goto L_0x052e
                java.lang.String r0 = r2.toUpperCase()
                r1.setCountry(r8, r0)
                goto L_0x0545
            L_0x052e:
                im.bclpbkiauv.tgnet.TLRPC$TL_help_getNearestDc r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_help_getNearestDc
                r0.<init>()
                im.bclpbkiauv.messenger.AccountInstance r3 = r44.getAccountInstance()
                im.bclpbkiauv.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$u8uyswZ-Etta8L20lbPXQ8jGmyM r9 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$PhoneView$u8uyswZ-Etta8L20lbPXQ8jGmyM
                r9.<init>(r8)
                r11 = 10
                r3.sendRequest(r0, r9, r11)
            L_0x0545:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x0564
                android.widget.TextView r0 = r1.countryButton
                r3 = 2131690589(0x7f0f045d, float:1.9010226E38)
                java.lang.String r9 = "ChooseCountry"
                java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r3)
                r0.setText(r3)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r3 = 0
                r0.setHintText(r3)
                r3 = 1
                r1.countryState = r3
            L_0x0564:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x057b
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.requestFocus()
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r3 = r0.length()
                r0.setSelection(r3)
                goto L_0x0580
            L_0x057b:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.requestFocus()
            L_0x0580:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.PhoneView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context):void");
        }

        public /* synthetic */ void lambda$new$2$HloginActivity$PhoneView(View view2) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    HloginActivity.PhoneView.this.lambda$null$1$HloginActivity$PhoneView(country);
                }
            });
            this.this$0.presentFragment(fragment);
        }

        public /* synthetic */ void lambda$null$1$HloginActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry((String) null, country);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    HloginActivity.PhoneView.this.lambda$null$0$HloginActivity$PhoneView();
                }
            }, 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        public /* synthetic */ void lambda$null$0$HloginActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        public /* synthetic */ boolean lambda$new$3$HloginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        public /* synthetic */ boolean lambda$new$4$HloginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ boolean lambda$new$5$HloginActivity$PhoneView(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
        }

        public /* synthetic */ void lambda$new$6$HloginActivity$PhoneView(View v) {
            onNextPressed();
        }

        public /* synthetic */ void lambda$new$7$HloginActivity$PhoneView(View v) {
            if (this.this$0.getParentActivity() != null) {
                HloginActivity hloginActivity = this.this$0;
                boolean unused = hloginActivity.syncContacts = !hloginActivity.syncContacts;
                ((CheckBoxCell) v).setChecked(this.this$0.syncContacts, true);
                if (this.this$0.syncContacts) {
                    ToastUtils.show((int) R.string.SyncContactsOn);
                } else {
                    ToastUtils.show((int) R.string.SyncContactsOff);
                }
            }
        }

        public /* synthetic */ void lambda$new$9$HloginActivity$PhoneView(HashMap languageMap, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(response, languageMap) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    HloginActivity.PhoneView.this.lambda$null$8$HloginActivity$PhoneView(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$8$HloginActivity$PhoneView(TLObject response, HashMap languageMap) {
            if (response != null) {
                TLRPC.TL_nearestDc res = (TLRPC.TL_nearestDc) response;
                if (this.codeField.length() == 0) {
                    setCountry(languageMap, res.country.toUpperCase());
                }
            }
        }

        public void selectCountry(String name, CountrySelectActivity.Country country) {
            String str = null;
            if (name != null) {
                if (this.countriesArray.indexOf(name) != -1) {
                    this.ignoreOnTextChange = true;
                    String code = this.countriesMap.get(name);
                    this.codeField.setText(code);
                    this.countryButton.setText(name);
                    String hint = this.phoneFormatMap.get(code);
                    HintEditText hintEditText = this.phoneField;
                    if (hint != null) {
                        str = hint.replace('X', Typography.ndash);
                    }
                    hintEditText.setHintText(str);
                    this.countryState = 0;
                    this.ignoreOnTextChange = false;
                }
            } else if (country != null) {
                this.ignoreOnTextChange = true;
                EditTextBoldCursor editTextBoldCursor = this.codeField;
                editTextBoldCursor.setText(country.code + "");
                if (country.phoneFormat != null) {
                    HintEditText hintEditText2 = this.phoneField;
                    if (country.phoneFormat != null) {
                        str = country.phoneFormat.replace('X', Typography.ndash);
                    }
                    hintEditText2.setHintText(str);
                }
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        private void setCountry(HashMap<String, String> languageMap, String country) {
            String countryName = languageMap.get(country);
            if (countryName != null && this.countriesArray.indexOf(countryName) != -1) {
                this.codeField.setText(this.countriesMap.get(countryName));
                this.countryState = 0;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed() {
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                int i = this.countryState;
                if (i == 1) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                } else if (i == 2 && !BuildVars.DEBUG_VERSION) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("WrongCountry", R.string.WrongCountry));
                } else if (this.codeField.length() == 0) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(PhoneFormat.stripExceptNumbers("" + this.codeField.getText()));
                    sb.append(" ");
                    sb.append(PhoneFormat.stripExceptNumbers("" + this.phoneField.getText()));
                    String phone = sb.toString();
                    if (this.this$0.getParentActivity() instanceof LaunchActivity) {
                        for (int a = 0; a < 3; a++) {
                            UserConfig userConfig = UserConfig.getInstance(a);
                            if (userConfig.isClientActivated() && PhoneNumberUtils.compare(phone, userConfig.getCurrentUser().phone)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn));
                                builder.setPositiveButton(LocaleController.getString("AccountSwitch", R.string.AccountSwitch), new DialogInterface.OnClickListener(a) {
                                    private final /* synthetic */ int f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.PhoneView.this.lambda$onNextPressed$10$HloginActivity$PhoneView(this.f$1, dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                                return;
                            }
                        }
                    }
                    ConnectionsManager.getInstance(this.this$0.currentAccount).cleanup(false);
                    TLRPC.TL_auth_sendCode req = new TLRPC.TL_auth_sendCode();
                    req.api_hash = BuildVars.APP_HASH;
                    req.api_id = BuildVars.APP_ID;
                    req.phone_number = phone;
                    req.settings = new TLRPC.TL_codeSettings();
                    req.settings.allow_flashcall = false;
                    req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (req.settings.allow_app_hash) {
                        preferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                    } else {
                        preferences.edit().remove("sms_hash").commit();
                    }
                    Bundle params = new Bundle();
                    params.putString("phone", Marker.ANY_NON_NULL_MARKER + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        params.putString("ephone", Marker.ANY_NON_NULL_MARKER + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        params.putString("ephone", Marker.ANY_NON_NULL_MARKER + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(params, req) {
                        private final /* synthetic */ Bundle f$1;
                        private final /* synthetic */ TLRPC.TL_auth_sendCode f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            HloginActivity.PhoneView.this.lambda$onNextPressed$12$HloginActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 27));
                }
            }
        }

        public /* synthetic */ void lambda$onNextPressed$10$HloginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(num, false);
            }
            this.this$0.finishFragment();
        }

        public /* synthetic */ void lambda$onNextPressed$12$HloginActivity$PhoneView(Bundle params, TLRPC.TL_auth_sendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, params, response, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ Bundle f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPC.TL_auth_sendCode f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    HloginActivity.PhoneView.this.lambda$null$11$HloginActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$11$HloginActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_sendCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    this.this$0.needShowInvalidAlert(req.phone_number, false);
                } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PhoneNumberFlood", R.string.PhoneNumberFlood));
                } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                    this.this$0.needShowInvalidAlert(req.phone_number, true);
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.code != -1000) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                }
            }
            this.this$0.needHideProgress(false);
        }

        public void fillNumber() {
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell2 = this.checkBoxCell;
            if (checkBoxCell2 != null) {
                checkBoxCell2.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    HloginActivity.PhoneView.this.lambda$onShow$13$HloginActivity$PhoneView();
                }
            }, 100);
        }

        public /* synthetic */ void lambda$onShow$13$HloginActivity$PhoneView() {
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                AndroidUtilities.showKeyboard(this.phoneField);
                return;
            }
            this.codeField.requestFocus();
            AndroidUtilities.showKeyboard(this.codeField);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourPhone", R.string.YourPhone);
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("phoneview_code", code);
            }
            String phone = this.phoneField.getText().toString();
            if (phone.length() != 0) {
                bundle.putString("phoneview_phone", phone);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            String code = bundle.getString("phoneview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
            String phone = bundle.getString("phoneview_phone");
            if (phone != null) {
                this.phoneField.setText(phone);
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        /* access modifiers changed from: private */
        public ImageView blackImageView;
        /* access modifiers changed from: private */
        public ImageView blueImageView;
        private String catchedPhone;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public int currentType;
        private String emailPhone;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange;
        private boolean isRestored;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        /* access modifiers changed from: private */
        public int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        /* access modifiers changed from: private */
        public int openTime;
        private String pattern = "*";
        private String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        /* access modifiers changed from: private */
        public String requestPhone;
        final /* synthetic */ HloginActivity this$0;
        /* access modifiers changed from: private */
        public int time = TimeConstants.MIN;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        /* access modifiers changed from: private */
        public int timeout;
        private final Object timerSync = new Object();
        /* access modifiers changed from: private */
        public TextView titleTextView;
        /* access modifiers changed from: private */
        public boolean waitingForEvent;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(im.bclpbkiauv.ui.hui.login.HloginActivity r32, android.content.Context r33, int r34) {
            /*
                r31 = this;
                r0 = r31
                r1 = r32
                r2 = r33
                r0.this$0 = r1
                r0.<init>(r2)
                java.lang.Object r3 = new java.lang.Object
                r3.<init>()
                r0.timerSync = r3
                r3 = 60000(0xea60, float:8.4078E-41)
                r0.time = r3
                r3 = 15000(0x3a98, float:2.102E-41)
                r0.codeTime = r3
                java.lang.String r3 = ""
                r0.lastError = r3
                java.lang.String r3 = "*"
                r0.pattern = r3
                r3 = r34
                r0.currentType = r3
                r4 = 1
                r0.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.confirmTextView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3f800000, float:1.0)
                r5.setLineSpacing(r9, r10)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r11 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r11)
                android.widget.TextView r5 = r0.titleTextView
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r13 = 3
                if (r11 == 0) goto L_0x007b
                r11 = 5
                goto L_0x007c
            L_0x007b:
                r11 = 3
            L_0x007c:
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.titleTextView
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 49
                r5.setGravity(r11)
                int r5 = r0.currentType
                r14 = -2
                if (r5 != r13) goto L_0x012a
                android.widget.TextView r5 = r0.confirmTextView
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x009d
                r9 = 5
                goto L_0x009e
            L_0x009d:
                r9 = 3
            L_0x009e:
                r9 = r9 | 48
                r5.setGravity(r9)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00ae
                r9 = 5
                goto L_0x00af
            L_0x00ae:
                r9 = 3
            L_0x00af:
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r5, r9)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r15 = 2131231426(0x7f0802c2, float:1.8078933E38)
                r9.setImageResource(r15)
                boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x00f9
                r16 = 1115684864(0x42800000, float:64.0)
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 19
                r19 = 1073741824(0x40000000, float:2.0)
                r20 = 1073741824(0x40000000, float:2.0)
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r15)
                android.widget.TextView r15 = r0.confirmTextView
                r16 = -1082130432(0xffffffffbf800000, float:-1.0)
                r17 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r18 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r18 == 0) goto L_0x00e7
                r18 = 5
                goto L_0x00e9
            L_0x00e7:
                r18 = 3
            L_0x00e9:
                r19 = 1118044160(0x42a40000, float:82.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r15, r12)
                goto L_0x0128
            L_0x00f9:
                android.widget.TextView r12 = r0.confirmTextView
                r15 = -1082130432(0xffffffffbf800000, float:-1.0)
                r16 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r17 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x0106
                r17 = 5
                goto L_0x0108
            L_0x0106:
                r17 = 3
            L_0x0108:
                r18 = 0
                r19 = 0
                r20 = 1118044160(0x42a40000, float:82.0)
                r21 = 0
                android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r5.addView(r12, r15)
                r16 = 1115684864(0x42800000, float:64.0)
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 21
                r20 = 1073741824(0x40000000, float:2.0)
                r22 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r12)
            L_0x0128:
                goto L_0x01da
            L_0x012a:
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r11)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
                int r12 = r0.currentType
                if (r12 != r4) goto L_0x01aa
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.blackImageView = r12
                r15 = 2131231607(0x7f080377, float:1.80793E38)
                r12.setImageResource(r15)
                android.widget.ImageView r12 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r15 = new android.graphics.PorterDuffColorFilter
                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
                r15.<init>(r9, r11)
                r12.setColorFilter(r15)
                android.widget.ImageView r9 = r0.blackImageView
                r24 = -1073741824(0xffffffffc0000000, float:-2.0)
                r25 = -1073741824(0xffffffffc0000000, float:-2.0)
                r26 = 51
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 0
                android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
                r5.addView(r9, r11)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r0.blueImageView = r9
                r11 = 2131231605(0x7f080375, float:1.8079296E38)
                r9.setImageResource(r11)
                android.widget.ImageView r9 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
                java.lang.String r12 = "chats_actionBackground"
                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
                android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
                r11.<init>(r12, r15)
                r9.setColorFilter(r11)
                android.widget.ImageView r9 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
                r5.addView(r9, r11)
                android.widget.TextView r9 = r0.titleTextView
                r11 = 2131693850(0x7f0f111a, float:1.901684E38)
                java.lang.String r12 = "SentAppCodeTitle"
                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
                r9.setText(r11)
                goto L_0x01b8
            L_0x01aa:
                android.widget.TextView r9 = r0.titleTextView
                r11 = 2131693854(0x7f0f111e, float:1.9016848E38)
                java.lang.String r12 = "SentSmsCodeTitle"
                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
                r9.setText(r11)
            L_0x01b8:
                android.widget.TextView r9 = r0.titleTextView
                r24 = -2
                r25 = -2
                r26 = 49
                r27 = 0
                r28 = 18
                r29 = 0
                r30 = 0
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
                r0.addView(r9, r11)
                android.widget.TextView r9 = r0.confirmTextView
                r28 = 17
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
                r0.addView(r9, r11)
            L_0x01da:
                android.view.View r5 = new android.view.View
                r5.<init>(r2)
                java.lang.String r9 = "windowBackgroundWhiteGrayLine"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r5.setBackgroundColor(r11)
                r24 = -1082130432(0xffffffffbf800000, float:-1.0)
                r25 = 1056964608(0x3f000000, float:0.5)
                r26 = 16
                r27 = 1098907648(0x41800000, float:16.0)
                r28 = 1116733440(0x42900000, float:72.0)
                r29 = 1098907648(0x41800000, float:16.0)
                r30 = 0
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r24, (float) r25, (int) r26, (float) r27, (float) r28, (float) r29, (float) r30)
                r0.addView(r5, r11)
                android.widget.LinearLayout r11 = new android.widget.LinearLayout
                r11.<init>(r2)
                r12 = 0
                r11.setOrientation(r12)
                r24 = -1
                r25 = 68
                r26 = 1
                r27 = 16
                r28 = 0
                r29 = 16
                r30 = 0
                android.widget.LinearLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
                r0.addView(r11, r15)
                android.widget.ImageView r15 = new android.widget.ImageView
                r15.<init>(r2)
                android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER_INSIDE
                r15.setScaleType(r4)
                androidx.fragment.app.FragmentActivity r4 = r32.getParentActivity()
                android.content.res.Resources r4 = r4.getResources()
                r7 = 2131558712(0x7f0d0138, float:1.8742748E38)
                android.graphics.drawable.Drawable r4 = r4.getDrawable(r7)
                android.graphics.drawable.Drawable r4 = r4.mutate()
                java.lang.String r7 = "actionBarDefaultIcon"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                android.graphics.drawable.Drawable r4 = im.bclpbkiauv.messenger.utils.DrawableUtils.tintDrawable(r4, r7)
                r15.setImageDrawable(r4)
                r7 = 16
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r7)
                r11.addView(r15, r7)
                android.view.View r7 = new android.view.View
                r7.<init>(r2)
                r5 = r7
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r5.setBackgroundColor(r7)
                r24 = 1056964608(0x3f000000, float:0.5)
                r25 = 1107820544(0x42080000, float:34.0)
                r26 = 16
                r27 = 1098907648(0x41800000, float:16.0)
                r28 = 0
                r29 = 1098907648(0x41800000, float:16.0)
                r30 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r24, (float) r25, (int) r26, (float) r27, (float) r28, (float) r29, (float) r30)
                r11.addView(r5, r7)
                android.widget.LinearLayout r7 = new android.widget.LinearLayout
                r7.<init>(r2)
                r0.codeFieldContainer = r7
                r7.setOrientation(r12)
                android.widget.LinearLayout r7 = r0.codeFieldContainer
                r24 = -2
                r25 = 34
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 0
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
                r11.addView(r7, r12)
                int r7 = r0.currentType
                if (r7 != r13) goto L_0x029a
                android.widget.LinearLayout r7 = r0.codeFieldContainer
                r12 = 8
                r7.setVisibility(r12)
            L_0x029a:
                android.view.View r7 = new android.view.View
                r7.<init>(r2)
                r5 = r7
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r5.setBackgroundColor(r7)
                r24 = -1082130432(0xffffffffbf800000, float:-1.0)
                r25 = 1056964608(0x3f000000, float:0.5)
                r26 = 16
                r27 = 1098907648(0x41800000, float:16.0)
                r28 = 0
                r29 = 1098907648(0x41800000, float:16.0)
                r30 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((float) r24, (float) r25, (int) r26, (float) r27, (float) r28, (float) r29, (float) r30)
                r0.addView(r5, r7)
                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$1 r7 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$1
                r7.<init>(r2, r1)
                r0.timeText = r7
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r7.setTextColor(r6)
                android.widget.TextView r6 = r0.timeText
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r7 = (float) r7
                r6.setLineSpacing(r7, r10)
                int r6 = r0.currentType
                r7 = 1097859072(0x41700000, float:15.0)
                r9 = 1092616192(0x41200000, float:10.0)
                if (r6 != r13) goto L_0x031d
                android.widget.TextView r6 = r0.timeText
                r12 = 1096810496(0x41600000, float:14.0)
                r13 = 1
                r6.setTextSize(r13, r12)
                android.widget.TextView r6 = r0.timeText
                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x02ec
                r12 = 5
                goto L_0x02ed
            L_0x02ec:
                r12 = 3
            L_0x02ed:
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r12)
                r0.addView(r6, r12)
                im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r6 = new im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView
                r6.<init>(r2)
                r0.progressView = r6
                android.widget.TextView r6 = r0.timeText
                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0303
                r12 = 5
                goto L_0x0304
            L_0x0303:
                r12 = 3
            L_0x0304:
                r6.setGravity(r12)
                im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r6 = r0.progressView
                r20 = -1
                r21 = 3
                r22 = 0
                r23 = 1094713344(0x41400000, float:12.0)
                r24 = 0
                r25 = 0
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r20, (int) r21, (float) r22, (float) r23, (float) r24, (float) r25)
                r0.addView(r6, r12)
                goto L_0x0341
            L_0x031d:
                android.widget.TextView r6 = r0.timeText
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r9 = 0
                r6.setPadding(r9, r12, r9, r13)
                android.widget.TextView r6 = r0.timeText
                r9 = 1
                r6.setTextSize(r9, r7)
                android.widget.TextView r6 = r0.timeText
                r9 = 49
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.timeText
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r6, r12)
            L_0x0341:
                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$2 r6 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$2
                r6.<init>(r2, r1)
                r0.problemText = r6
                java.lang.String r9 = "windowBackgroundWhiteBlueText4"
                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r6.setTextColor(r9)
                android.widget.TextView r6 = r0.problemText
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r6.setLineSpacing(r9, r10)
                android.widget.TextView r6 = r0.problemText
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                r10 = 1092616192(0x41200000, float:10.0)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                r10 = 0
                r6.setPadding(r10, r9, r10, r12)
                android.widget.TextView r6 = r0.problemText
                r9 = 1
                r6.setTextSize(r9, r7)
                android.widget.TextView r6 = r0.problemText
                r7 = 49
                r6.setGravity(r7)
                int r6 = r0.currentType
                if (r6 != r9) goto L_0x038b
                android.widget.TextView r6 = r0.problemText
                r7 = 2131690916(0x7f0f05a4, float:1.901089E38)
                java.lang.String r9 = "DidNotGetTheCodeSms"
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
                r6.setText(r7)
                goto L_0x0399
            L_0x038b:
                android.widget.TextView r6 = r0.problemText
                r7 = 2131690915(0x7f0f05a3, float:1.9010887E38)
                java.lang.String r9 = "DidNotGetTheCode"
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
                r6.setText(r7)
            L_0x0399:
                android.widget.TextView r6 = r0.problemText
                r20 = -2
                r21 = -2
                r22 = 49
                r23 = 0
                r24 = 50
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r6, r7)
                android.widget.TextView r6 = r0.problemText
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$gjNGm8WqJLmISDKXtUBRJez1jE4 r7 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$gjNGm8WqJLmISDKXtUBRJez1jE4
                r7.<init>()
                r6.setOnClickListener(r7)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r7 = 2131692179(0x7f0f0a93, float:1.901345E38)
                java.lang.String r9 = "Next"
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
                java.lang.String r7 = r7.toUpperCase()
                r6.setText(r7)
                r7 = 17
                r6.setGravity(r7)
                r7 = -1
                r6.setTextColor(r7)
                r7 = 1098907648(0x41800000, float:16.0)
                r9 = 1
                r6.setTextSize(r9, r7)
                r7 = 1103101952(0x41c00000, float:24.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                java.lang.String r9 = "#FF268CFF"
                int r9 = android.graphics.Color.parseColor(r9)
                java.lang.String r10 = "#FF1E69BD"
                int r10 = android.graphics.Color.parseColor(r10)
                android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorRoundRectDrawable(r7, r9, r10)
                r6.setBackground(r7)
                int r7 = android.os.Build.VERSION.SDK_INT
                r9 = 21
                if (r7 < r9) goto L_0x0457
                android.animation.StateListAnimator r7 = new android.animation.StateListAnimator
                r7.<init>()
                r9 = 1
                int[] r10 = new int[r9]
                r12 = 16842919(0x10100a7, float:2.3694026E-38)
                r13 = 0
                r10[r13] = r12
                r12 = 2
                float[] r14 = new float[r12]
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r12 = (float) r12
                r14[r13] = r12
                r12 = 1082130432(0x40800000, float:4.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                float r8 = (float) r8
                r14[r9] = r8
                java.lang.String r8 = "translationZ"
                android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r6, r8, r14)
                r12 = 200(0xc8, double:9.9E-322)
                android.animation.ObjectAnimator r9 = r9.setDuration(r12)
                r7.addState(r10, r9)
                r9 = 0
                int[] r10 = new int[r9]
                r14 = 2
                float[] r14 = new float[r14]
                r16 = 1082130432(0x40800000, float:4.0)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                float r12 = (float) r12
                r14[r9] = r12
                r9 = 1073741824(0x40000000, float:2.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                float r9 = (float) r9
                r12 = 1
                r14[r12] = r9
                android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r6, r8, r14)
                r12 = 200(0xc8, double:9.9E-322)
                android.animation.ObjectAnimator r8 = r8.setDuration(r12)
                r7.addState(r10, r8)
                r6.setStateListAnimator(r7)
            L_0x0457:
                r7 = 1101004800(0x41a00000, float:20.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r9 = 1092616192(0x41200000, float:10.0)
                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r6.setPadding(r8, r10, r7, r9)
                r16 = -1
                r17 = -2
                r18 = 17
                r19 = 32
                r20 = 50
                r21 = 32
                r22 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
                r0.addView(r6, r7)
                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$ICEsPolNoVG0iJt-QNuG19HeowY r7 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$ICEsPolNoVG0iJt-QNuG19HeowY
                r7.<init>()
                r6.setOnClickListener(r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context, int):void");
        }

        public /* synthetic */ void lambda$new$0$HloginActivity$LoginActivitySmsView(View v) {
            if (!this.nextPressed) {
                if ((this.nextType == 4 && this.currentType == 2) || this.nextType == 0) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                        Intent mailer = new Intent("android.intent.action.SEND");
                        mailer.setType("message/rfc822");
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                        mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                    } catch (Exception e) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                    }
                } else if (this.this$0.doneProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        public /* synthetic */ void lambda$new$1$HloginActivity$LoginActivitySmsView(View v) {
            onNextPressed();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            ImageView imageView;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.currentType != 3 && (imageView = this.blueImageView) != null) {
                int innerHeight = imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (this.this$0.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else if (this.this$0.scrollHeight > maxHeight) {
                    setMeasuredDimension(getMeasuredWidth(), maxHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), this.this$0.scrollHeight);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int t2;
            super.onLayout(changed, l, t, r, b);
            if (this.currentType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int h = this.problemText.getMeasuredHeight();
                    t2 = (bottom + height) - h;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
                } else {
                    t2 = bottom + height;
                }
                int h3 = this.codeFieldContainer.getMeasuredHeight();
                int t3 = (((t2 - bottom) - h3) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), t3, this.codeFieldContainer.getRight(), t3 + h3);
                int height2 = t3;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            int sendRequest = ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(params) {
                private final /* synthetic */ Bundle f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    HloginActivity.LoginActivitySmsView.this.lambda$resendCode$3$HloginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                }
            }, 10);
            this.this$0.needShowProgress(0);
        }

        public /* synthetic */ void lambda$resendCode$3$HloginActivity$LoginActivitySmsView(Bundle params, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, params, response) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ Bundle f$2;
                private final /* synthetic */ TLObject f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    HloginActivity.LoginActivitySmsView.this.lambda$null$2$HloginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$HloginActivity$LoginActivitySmsView(TLRPC.TL_error error, Bundle params, TLObject response) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    this.this$0.setPage(0, true, (Bundle) null, true);
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.code != -1000) {
                    HloginActivity hloginActivity = this.this$0;
                    String string = LocaleController.getString("AppName", R.string.AppName);
                    hloginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                }
            }
            this.this$0.needHideProgress(false);
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        public boolean needBackButton() {
            return true;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
                this.isRestored = restore;
                this.waitingForEvent = true;
                int i3 = this.currentType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.emailPhone = bundle.getString("ephone");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                int i4 = bundle.getInt("timeout");
                this.time = i4;
                this.timeout = i4;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                int i5 = bundle.getInt("length");
                this.length = i5;
                if (i5 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                Drawable drawable = null;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    int a = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (a >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[a].setText("");
                        a++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    int a2 = 0;
                    while (a2 < this.length) {
                        final int num = a2;
                        this.codeField[a2] = new EditTextBoldCursor(getContext());
                        this.codeField[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[a2].setCursorWidth(1.5f);
                        Drawable pressedDrawable = getResources().getDrawable(R.drawable.search_dark_activated).mutate();
                        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), PorterDuff.Mode.MULTIPLY));
                        this.codeField[a2].setBackgroundDrawable(pressedDrawable);
                        this.codeField[a2].setImeOptions(268435461);
                        this.codeField[a2].setTextSize(1, 20.0f);
                        this.codeField[a2].setMaxLines(1);
                        this.codeField[a2].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[a2].setPadding(0, 0, 0, 0);
                        this.codeField[a2].setBackground(drawable);
                        this.codeField[a2].setGravity(49);
                        if (this.currentType == 3) {
                            this.codeField[a2].setEnabled(false);
                            this.codeField[a2].setInputType(0);
                            this.codeField[a2].setVisibility(8);
                        } else {
                            this.codeField[a2].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[a2], LayoutHelper.createLinear(34, 36, 1, 0, 0, a2 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[a2].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                int len;
                                if (!LoginActivitySmsView.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                                    if (len > 1) {
                                        String text = s.toString();
                                        boolean unused = LoginActivitySmsView.this.ignoreOnTextChange = true;
                                        for (int a = 0; a < Math.min(LoginActivitySmsView.this.length - num, len); a++) {
                                            if (a == 0) {
                                                s.replace(0, len, text.substring(a, a + 1));
                                            } else {
                                                LoginActivitySmsView.this.codeField[num + a].setText(text.substring(a, a + 1));
                                            }
                                        }
                                        boolean unused2 = LoginActivitySmsView.this.ignoreOnTextChange = false;
                                    }
                                    if (num != LoginActivitySmsView.this.length - 1) {
                                        LoginActivitySmsView.this.codeField[num + 1].setSelection(LoginActivitySmsView.this.codeField[num + 1].length());
                                        LoginActivitySmsView.this.codeField[num + 1].requestFocus();
                                    }
                                    if ((num == LoginActivitySmsView.this.length - 1 || (num == LoginActivitySmsView.this.length - 2 && len >= 2)) && LoginActivitySmsView.this.getCode().length() == LoginActivitySmsView.this.length) {
                                        LoginActivitySmsView.this.onNextPressed();
                                    }
                                }
                            }
                        });
                        this.codeField[a2].setOnKeyListener(new View.OnKeyListener(num) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                                return HloginActivity.LoginActivitySmsView.this.lambda$setParams$4$HloginActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
                            }
                        });
                        this.codeField[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                return HloginActivity.LoginActivitySmsView.this.lambda$setParams$5$HloginActivity$LoginActivitySmsView(textView, i, keyEvent);
                            }
                        });
                        a2++;
                        drawable = null;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    int i6 = this.currentType;
                    if (i6 == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", R.string.SentAppCode));
                    } else if (i6 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (i6 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (i6 == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    int i7 = this.currentType;
                    if (i7 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (i7 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i8 = this.nextType;
                        if (i8 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                        } else if (i8 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                        }
                        String callLogNumber = this.isRestored ? AndroidUtilities.obtainLoginPhoneCall(this.pattern) : null;
                        if (callLogNumber != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(callLogNumber);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                            return;
                        }
                        String str2 = this.catchedPhone;
                        if (str2 != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(str2);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                            return;
                        }
                        createTimer();
                    } else if (this.currentType == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        String hash = preferences.getString("sms_hash", (String) null);
                        String savedCode = null;
                        if (!TextUtils.isEmpty(hash)) {
                            String savedCode2 = preferences.getString("sms_hash_code", (String) null);
                            if (savedCode2 != null) {
                                if (savedCode2.contains(hash + LogUtils.VERTICAL)) {
                                    savedCode = savedCode2.substring(savedCode2.indexOf(124) + 1);
                                }
                            }
                            savedCode = null;
                        }
                        if (savedCode != null) {
                            this.codeField[0].setText(savedCode);
                            onNextPressed();
                            return;
                        }
                        createTimer();
                    } else if (this.currentType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$4$HloginActivity$LoginActivitySmsView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        public /* synthetic */ boolean lambda$setParams$5$HloginActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                              (wrap: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI) = 
                              (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR)
                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.4.run():void, dex: classes6.dex
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
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI) = 
                              (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.4.run():void, dex: classes6.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI r0 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$4$8ReufJF99dltPRLOxjn4tnT3AMI
                            r0.<init>(r1)
                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.AnonymousClass4.run():void");
                    }

                    public /* synthetic */ void lambda$run$0$HloginActivity$LoginActivitySmsView$4() {
                        double currentTime = (double) System.currentTimeMillis();
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                        int unused2 = loginActivitySmsView.codeTime = (int) (((double) loginActivitySmsView.codeTime) - (currentTime - LoginActivitySmsView.this.lastCodeTime));
                        if (LoginActivitySmsView.this.codeTime <= 1000) {
                            LoginActivitySmsView.this.problemText.setVisibility(0);
                            LoginActivitySmsView.this.timeText.setVisibility(8);
                            LoginActivitySmsView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.codeTimer != null) {
                        this.codeTimer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000e: INVOKE  
                                  (wrap: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM : 0x000b: CONSTRUCTOR  (r0v2 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM) = 
                                  (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                 call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
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
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: CONSTRUCTOR  (r0v2 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM) = 
                                  (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                 call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 90 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 96 more
                                */
                            /*
                                this = this;
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r0 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                java.util.Timer r0 = r0.timeTimer
                                if (r0 != 0) goto L_0x0009
                                return
                            L_0x0009:
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM r0 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$5OW4o81GjzP3Ah-E6Yj1KYA2qaM
                                r0.<init>(r1)
                                im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.AnonymousClass5.run():void");
                        }

                        public /* synthetic */ void lambda$run$2$HloginActivity$LoginActivitySmsView$5() {
                            double currentTime = (double) System.currentTimeMillis();
                            double unused = LoginActivitySmsView.this.lastCurrentTime = currentTime;
                            LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                            int unused2 = loginActivitySmsView.time = (int) (((double) loginActivitySmsView.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                            if (LoginActivitySmsView.this.time >= 1000) {
                                int minutes = (LoginActivitySmsView.this.time / 1000) / 60;
                                int seconds = (LoginActivitySmsView.this.time / 1000) - (minutes * 60);
                                if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                } else if (LoginActivitySmsView.this.nextType == 2) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                }
                                if (LoginActivitySmsView.this.progressView != null) {
                                    LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                    return;
                                }
                                return;
                            }
                            if (LoginActivitySmsView.this.progressView != null) {
                                LoginActivitySmsView.this.progressView.setProgress(1.0f);
                            }
                            LoginActivitySmsView.this.destroyTimer();
                            if (LoginActivitySmsView.this.currentType == 3) {
                                AndroidUtilities.setWaitingForCall(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                                boolean unused3 = LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            } else if (LoginActivitySmsView.this.currentType != 2 && LoginActivitySmsView.this.currentType != 4) {
                            } else {
                                if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2) {
                                    if (LoginActivitySmsView.this.nextType == 4) {
                                        LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                                    } else {
                                        LoginActivitySmsView.this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                                    }
                                    LoginActivitySmsView.this.createCodeTimer();
                                    TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                                    req.phone_number = LoginActivitySmsView.this.requestPhone;
                                    req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, 
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x019a: INVOKE  
                                          (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x018f: INVOKE  (r5v12 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                          (wrap: int : 0x018b: INVOKE  (r5v11 int) = 
                                          (wrap: im.bclpbkiauv.ui.hui.login.HloginActivity : 0x0189: IGET  (r5v10 im.bclpbkiauv.ui.hui.login.HloginActivity) = 
                                          (wrap: im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView : 0x0187: IGET  (r5v9 im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                         im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.this$1 im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView)
                                         im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this$0 im.bclpbkiauv.ui.hui.login.HloginActivity)
                                         im.bclpbkiauv.ui.hui.login.HloginActivity.access$5300(im.bclpbkiauv.ui.hui.login.HloginActivity):int type: STATIC)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                          (r4v16 'req' im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode)
                                          (wrap: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE : 0x0195: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                         call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR)
                                          (10 int)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate, int):int type: VIRTUAL in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.lambda$run$2$HloginActivity$LoginActivitySmsView$5():void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
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
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:156)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0195: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                         call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.lambda$run$2$HloginActivity$LoginActivitySmsView$5():void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 99 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 105 more
                                        */
                                    /*
                                        this = this;
                                        long r0 = java.lang.System.currentTimeMillis()
                                        double r0 = (double) r0
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r2 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        double r2 = r2.lastCurrentTime
                                        double r2 = r0 - r2
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        double unused = r4.lastCurrentTime = r0
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r5 = r4.time
                                        double r5 = (double) r5
                                        double r5 = r5 - r2
                                        int r5 = (int) r5
                                        int unused = r4.time = r5
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.time
                                        r5 = 1065353216(0x3f800000, float:1.0)
                                        r6 = 3
                                        r7 = 1000(0x3e8, float:1.401E-42)
                                        r8 = 4
                                        r9 = 2
                                        r10 = 0
                                        if (r4 < r7) goto L_0x00bf
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.time
                                        int r4 = r4 / r7
                                        int r4 = r4 / 60
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r11 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r11 = r11.time
                                        int r11 = r11 / r7
                                        int r7 = r4 * 60
                                        int r11 = r11 - r7
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r7 = r7.nextType
                                        r12 = 1
                                        if (r7 == r8) goto L_0x007c
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r7 = r7.nextType
                                        if (r7 != r6) goto L_0x0053
                                        goto L_0x007c
                                    L_0x0053:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r6 = r6.nextType
                                        if (r6 != r9) goto L_0x009c
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        android.widget.TextView r6 = r6.timeText
                                        r7 = 2131694001(0x7f0f11b1, float:1.9017146E38)
                                        java.lang.Object[] r8 = new java.lang.Object[r9]
                                        java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
                                        r8[r10] = r9
                                        java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                                        r8[r12] = r9
                                        java.lang.String r9 = "SmsText"
                                        java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r9, r7, r8)
                                        r6.setText(r7)
                                        goto L_0x009c
                                    L_0x007c:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        android.widget.TextView r6 = r6.timeText
                                        r7 = 2131690300(0x7f0f033c, float:1.900964E38)
                                        java.lang.Object[] r8 = new java.lang.Object[r9]
                                        java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
                                        r8[r10] = r9
                                        java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                                        r8[r12] = r9
                                        java.lang.String r9 = "CallText"
                                        java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r9, r7, r8)
                                        r6.setText(r7)
                                    L_0x009c:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r6 = r6.progressView
                                        if (r6 == 0) goto L_0x00bd
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r6 = r6.progressView
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r7 = r7.time
                                        float r7 = (float) r7
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r8 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r8 = r8.timeout
                                        float r8 = (float) r8
                                        float r7 = r7 / r8
                                        float r5 = r5 - r7
                                        r6.setProgress(r5)
                                    L_0x00bd:
                                        goto L_0x019e
                                    L_0x00bf:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r4 = r4.progressView
                                        if (r4 == 0) goto L_0x00d0
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$ProgressView r4 = r4.progressView
                                        r4.setProgress(r5)
                                    L_0x00d0:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.destroyTimer()
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 != r6) goto L_0x00fa
                                        im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForCall(r10)
                                        im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                        int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveCall
                                        r4.removeObserver(r13, r5)
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        boolean unused = r4.waitingForEvent = r10
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.destroyCodeTimer()
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.resendCode()
                                        goto L_0x019e
                                    L_0x00fa:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 == r9) goto L_0x010a
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 != r8) goto L_0x019e
                                    L_0x010a:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 == r8) goto L_0x0140
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r9) goto L_0x011b
                                        goto L_0x0140
                                    L_0x011b:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r6) goto L_0x013f
                                        im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForSms(r10)
                                        im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                        int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveSmsCode
                                        r4.removeObserver(r13, r5)
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        boolean unused = r4.waitingForEvent = r10
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.destroyCodeTimer()
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.resendCode()
                                        goto L_0x019e
                                    L_0x013f:
                                        goto L_0x019e
                                    L_0x0140:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r8) goto L_0x015b
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        android.widget.TextView r4 = r4.timeText
                                        r5 = 2131690302(0x7f0f033e, float:1.9009644E38)
                                        java.lang.String r6 = "Calling"
                                        java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                        r4.setText(r5)
                                        goto L_0x016d
                                    L_0x015b:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        android.widget.TextView r4 = r4.timeText
                                        r5 = 2131693842(0x7f0f1112, float:1.9016824E38)
                                        java.lang.String r6 = "SendingSms"
                                        java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                        r4.setText(r5)
                                    L_0x016d:
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        r4.createCodeTimer()
                                        im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode
                                        r4.<init>()
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        java.lang.String r5 = r5.requestPhone
                                        r4.phone_number = r5
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        java.lang.String r5 = r5.phoneHash
                                        r4.phone_code_hash = r5
                                        im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.hui.login.HloginActivity r5 = r5.this$0
                                        int r5 = r5.currentAccount
                                        im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
                                        im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE r6 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$Ft1d6Yi4Lb4CrNtFw_oeUUU6LQE
                                        r6.<init>(r13)
                                        r7 = 10
                                        r5.sendRequest(r4, r6, r7)
                                        goto L_0x013f
                                    L_0x019e:
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.AnonymousClass5.lambda$run$2$HloginActivity$LoginActivitySmsView$5():void");
                                }

                                public /* synthetic */ void lambda$null$1$HloginActivity$LoginActivitySmsView$5(TLObject response, TLRPC.TL_error error) {
                                    if (error != null && error.text != null) {
                                        AndroidUtilities.runOnUIThread(
                                        /*  JADX ERROR: Method code generation error
                                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                              (wrap: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg : 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg) = 
                                              (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                              (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                             call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR)
                                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.lambda$null$1$HloginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
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
                                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg) = 
                                              (r1v0 'this' im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5 A[THIS])
                                              (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                             call: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivitySmsView$5, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.5.lambda$null$1$HloginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	... 90 more
                                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg, state: NOT_LOADED
                                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	... 96 more
                                            */
                                        /*
                                            this = this;
                                            if (r3 == 0) goto L_0x000e
                                            java.lang.String r0 = r3.text
                                            if (r0 == 0) goto L_0x000e
                                            im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg r0 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivitySmsView$5$-05f2HzSZbfd91AsBTZPS1aQ_Fg
                                            r0.<init>(r1, r3)
                                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                        L_0x000e:
                                            return
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivitySmsView.AnonymousClass5.lambda$null$1$HloginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                                    }

                                    public /* synthetic */ void lambda$null$0$HloginActivity$LoginActivitySmsView$5(TLRPC.TL_error error) {
                                        String unused = LoginActivitySmsView.this.lastError = error.text;
                                    }
                                }, 0, 1000);
                            }
                        }

                        /* access modifiers changed from: private */
                        public void destroyTimer() {
                            try {
                                synchronized (this.timerSync) {
                                    if (this.timeTimer != null) {
                                        this.timeTimer.cancel();
                                        this.timeTimer = null;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }

                        /* access modifiers changed from: private */
                        public String getCode() {
                            if (this.codeField == null) {
                                return "";
                            }
                            StringBuilder codeBuilder = new StringBuilder();
                            int a = 0;
                            while (true) {
                                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                                if (a >= editTextBoldCursorArr.length) {
                                    return codeBuilder.toString();
                                }
                                codeBuilder.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[a].getText().toString()));
                                a++;
                            }
                        }

                        public void onNextPressed() {
                            if (!this.nextPressed && this.this$0.currentViewNum >= 1 && this.this$0.currentViewNum <= 4) {
                                String code = getCode();
                                if (TextUtils.isEmpty(code)) {
                                    AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
                                    return;
                                }
                                this.nextPressed = true;
                                int i = this.currentType;
                                if (i == 2) {
                                    AndroidUtilities.setWaitingForSms(false);
                                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                } else if (i == 3) {
                                    AndroidUtilities.setWaitingForCall(false);
                                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                                }
                                this.waitingForEvent = false;
                                TLRPC.TL_auth_signIn req = new TLRPC.TL_auth_signIn();
                                req.phone_number = this.requestPhone;
                                req.phone_code = code;
                                req.phone_code_hash = this.phoneHash;
                                destroyTimer();
                                this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(req) {
                                    private final /* synthetic */ TLRPC.TL_auth_signIn f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        HloginActivity.LoginActivitySmsView.this.lambda$onNextPressed$9$HloginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                                    }
                                }, 10));
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$9$HloginActivity$LoginActivitySmsView(TLRPC.TL_auth_signIn req, TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;
                                private final /* synthetic */ TLRPC.TL_auth_signIn f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivitySmsView.this.lambda$null$8$HloginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$8$HloginActivity$LoginActivitySmsView(TLRPC.TL_error error, TLObject response, TLRPC.TL_auth_signIn req) {
                            EditTextBoldCursor[] editTextBoldCursorArr;
                            int i;
                            int i2;
                            boolean ok = false;
                            if (error == null) {
                                this.nextPressed = false;
                                ok = true;
                                this.this$0.needHideProgress(false);
                                destroyTimer();
                                destroyCodeTimer();
                                if (response instanceof TLRPC.TL_auth_authorizationSignUpRequired) {
                                    TLRPC.TL_auth_authorizationSignUpRequired authorization = (TLRPC.TL_auth_authorizationSignUpRequired) response;
                                    if (authorization.terms_of_service != null) {
                                        TLRPC.TL_help_termsOfService unused = this.this$0.currentTermsOfService = authorization.terms_of_service;
                                    }
                                    Bundle params = new Bundle();
                                    params.putString("phoneFormated", this.requestPhone);
                                    params.putString("phoneHash", this.phoneHash);
                                    params.putString("code", req.phone_code);
                                    this.this$0.setPage(5, true, params, false);
                                } else {
                                    this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                                }
                            } else {
                                this.lastError = error.text;
                                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                                    ok = true;
                                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(req) {
                                        private final /* synthetic */ TLRPC.TL_auth_signIn f$1;

                                        {
                                            this.f$1 = r2;
                                        }

                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            HloginActivity.LoginActivitySmsView.this.lambda$null$7$HloginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                                        }
                                    }, 10);
                                    destroyTimer();
                                    destroyCodeTimer();
                                } else {
                                    this.this$0.needHideProgress(false);
                                    if ((this.currentType == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((this.currentType == 2 && ((i = this.nextType) == 4 || i == 3)) || (this.currentType == 4 && this.nextType == 2))) {
                                        createTimer();
                                    }
                                    int i3 = this.currentType;
                                    if (i3 == 2) {
                                        AndroidUtilities.setWaitingForSms(true);
                                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                                    } else if (i3 == 3) {
                                        AndroidUtilities.setWaitingForCall(true);
                                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                                    }
                                    this.waitingForEvent = true;
                                    if (this.currentType != 3) {
                                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                            int a = 0;
                                            while (true) {
                                                editTextBoldCursorArr = this.codeField;
                                                if (a >= editTextBoldCursorArr.length) {
                                                    break;
                                                }
                                                editTextBoldCursorArr[a].setText("");
                                                a++;
                                            }
                                            editTextBoldCursorArr[0].requestFocus();
                                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                            onBackPressed(true);
                                            this.this$0.setPage(0, true, (Bundle) null, true);
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else {
                                            HloginActivity hloginActivity = this.this$0;
                                            String string = LocaleController.getString("AppName", R.string.AppName);
                                            hloginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                        }
                                    }
                                }
                            }
                            if (ok && this.currentType == 3) {
                                AndroidUtilities.endIncomingCall();
                            }
                        }

                        public /* synthetic */ void lambda$null$7$HloginActivity$LoginActivitySmsView(TLRPC.TL_auth_signIn req, TLObject response1, TLRPC.TL_error error1) {
                            AndroidUtilities.runOnUIThread(new Runnable(error1, response1, req) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;
                                private final /* synthetic */ TLRPC.TL_auth_signIn f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivitySmsView.this.lambda$null$6$HloginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$6$HloginActivity$LoginActivitySmsView(TLRPC.TL_error error1, TLObject response1, TLRPC.TL_auth_signIn req) {
                            this.nextPressed = false;
                            this.this$0.needHideProgress(false);
                            if (error1 == null) {
                                TLRPC.TL_account_password password = (TLRPC.TL_account_password) response1;
                                if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                                    return;
                                }
                                Bundle bundle = new Bundle();
                                if (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                                    TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) password.current_algo;
                                    bundle.putString("current_salt1", Utilities.bytesToHex(algo.salt1));
                                    bundle.putString("current_salt2", Utilities.bytesToHex(algo.salt2));
                                    bundle.putString("current_p", Utilities.bytesToHex(algo.p));
                                    bundle.putInt("current_g", algo.g);
                                    bundle.putString("current_srp_B", Utilities.bytesToHex(password.srp_B));
                                    bundle.putLong("current_srp_id", password.srp_id);
                                    bundle.putInt("passwordType", 1);
                                }
                                String str = "";
                                bundle.putString(TrackReferenceTypeBox.TYPE1, password.hint != null ? password.hint : str);
                                if (password.email_unconfirmed_pattern != null) {
                                    str = password.email_unconfirmed_pattern;
                                }
                                bundle.putString("email_unconfirmed_pattern", str);
                                bundle.putString("phoneFormated", this.requestPhone);
                                bundle.putString("phoneHash", this.phoneHash);
                                bundle.putString("code", req.phone_code);
                                bundle.putInt("has_recovery", password.has_recovery ? 1 : 0);
                                this.this$0.setPage(6, true, bundle, false);
                                return;
                            }
                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error1.text);
                        }

                        public boolean onBackPressed(boolean force) {
                            if (!force) {
                                XDialog.Builder builder = new XDialog.Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                                builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivitySmsView.this.lambda$onBackPressed$10$HloginActivity$LoginActivitySmsView(dialogInterface, i);
                                    }
                                });
                                this.this$0.showDialog(builder.create());
                                return false;
                            }
                            this.nextPressed = false;
                            this.this$0.needHideProgress(true);
                            TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
                            req.phone_number = this.requestPhone;
                            req.phone_code_hash = this.phoneHash;
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, $$Lambda$HloginActivity$LoginActivitySmsView$WI1p4GY9JeNQhgvvvshWkwVIJA4.INSTANCE, 10);
                            destroyTimer();
                            destroyCodeTimer();
                            this.currentParams = null;
                            int i = this.currentType;
                            if (i == 2) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            } else if (i == 3) {
                                AndroidUtilities.setWaitingForCall(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                            }
                            this.waitingForEvent = false;
                            return true;
                        }

                        public /* synthetic */ void lambda$onBackPressed$10$HloginActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }

                        static /* synthetic */ void lambda$onBackPressed$11(TLObject response, TLRPC.TL_error error) {
                        }

                        public void onDestroyActivity() {
                            super.onDestroyActivity();
                            int i = this.currentType;
                            if (i == 2) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            } else if (i == 3) {
                                AndroidUtilities.setWaitingForCall(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                            }
                            this.waitingForEvent = false;
                            destroyTimer();
                            destroyCodeTimer();
                        }

                        public void onShow() {
                            super.onShow();
                            if (this.currentType != 3) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        HloginActivity.LoginActivitySmsView.this.lambda$onShow$12$HloginActivity$LoginActivitySmsView();
                                    }
                                }, 100);
                            }
                        }

                        public /* synthetic */ void lambda$onShow$12$HloginActivity$LoginActivitySmsView() {
                            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                            if (editTextBoldCursorArr != null) {
                                for (int a = editTextBoldCursorArr.length - 1; a >= 0; a--) {
                                    if (a == 0 || this.codeField[a].length() != 0) {
                                        this.codeField[a].requestFocus();
                                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                                        editTextBoldCursorArr2[a].setSelection(editTextBoldCursorArr2[a].length());
                                        AndroidUtilities.showKeyboard(this.codeField[a]);
                                        return;
                                    }
                                }
                            }
                        }

                        public void didReceivedNotification(int id, int account, Object... args) {
                            if (this.waitingForEvent && this.codeField != null) {
                                if (id == NotificationCenter.didReceiveSmsCode) {
                                    this.codeField[0].setText("" + args[0]);
                                    onNextPressed();
                                } else if (id == NotificationCenter.didReceiveCall) {
                                    String num = "" + args[0];
                                    if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                                        if (!this.pattern.equals("*")) {
                                            this.catchedPhone = num;
                                            AndroidUtilities.endIncomingCall();
                                        }
                                        this.ignoreOnTextChange = true;
                                        this.codeField[0].setText(num);
                                        this.ignoreOnTextChange = false;
                                        onNextPressed();
                                    }
                                }
                            }
                        }

                        public void saveStateParams(Bundle bundle) {
                            String code = getCode();
                            if (code.length() != 0) {
                                bundle.putString("smsview_code_" + this.currentType, code);
                            }
                            String str = this.catchedPhone;
                            if (str != null) {
                                bundle.putString("catchedPhone", str);
                            }
                            if (this.currentParams != null) {
                                bundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
                            }
                            int i = this.time;
                            if (i != 0) {
                                bundle.putInt("time", i);
                            }
                            int i2 = this.openTime;
                            if (i2 != 0) {
                                bundle.putInt("open", i2);
                            }
                        }

                        public void restoreStateParams(Bundle bundle) {
                            EditTextBoldCursor[] editTextBoldCursorArr;
                            Bundle bundle2 = bundle.getBundle("smsview_params_" + this.currentType);
                            this.currentParams = bundle2;
                            if (bundle2 != null) {
                                setParams(bundle2, true);
                            }
                            String catched = bundle.getString("catchedPhone");
                            if (catched != null) {
                                this.catchedPhone = catched;
                            }
                            String code = bundle.getString("smsview_code_" + this.currentType);
                            if (!(code == null || (editTextBoldCursorArr = this.codeField) == null)) {
                                editTextBoldCursorArr[0].setText(code);
                            }
                            int t = bundle.getInt("time");
                            if (t != 0) {
                                this.time = t;
                            }
                            int t2 = bundle.getInt("open");
                            if (t2 != 0) {
                                this.openTime = t2;
                            }
                        }
                    }

                    public class LoginActivityPasswordView extends SlideView {
                        /* access modifiers changed from: private */
                        public TextView cancelButton;
                        /* access modifiers changed from: private */
                        public EditTextBoldCursor codeField;
                        /* access modifiers changed from: private */
                        public TextView confirmTextView;
                        private Bundle currentParams;
                        private int current_g;
                        private byte[] current_p;
                        /* access modifiers changed from: private */
                        public byte[] current_salt1;
                        /* access modifiers changed from: private */
                        public byte[] current_salt2;
                        private byte[] current_srp_B;
                        private long current_srp_id;
                        private String email_unconfirmed_pattern;
                        private boolean has_recovery;
                        private String hint;
                        private boolean nextPressed;
                        /* access modifiers changed from: private */
                        public int passwordType;
                        private String phoneCode;
                        private String phoneHash;
                        private String requestPhone;
                        /* access modifiers changed from: private */
                        public TextView resetAccountButton;
                        /* access modifiers changed from: private */
                        public TextView resetAccountText;
                        final /* synthetic */ HloginActivity this$0;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityPasswordView(im.bclpbkiauv.ui.hui.login.HloginActivity r21, android.content.Context r22) {
                            /*
                                r20 = this;
                                r0 = r20
                                r1 = r22
                                r2 = r21
                                r0.this$0 = r2
                                r0.<init>(r1)
                                r3 = 1
                                r0.setOrientation(r3)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.confirmTextView = r4
                                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r6)
                                android.widget.TextView r4 = r0.confirmTextView
                                r6 = 1096810496(0x41600000, float:14.0)
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r8 = 5
                                r9 = 3
                                if (r7 == 0) goto L_0x0030
                                r7 = 5
                                goto L_0x0031
                            L_0x0030:
                                r7 = 3
                            L_0x0031:
                                r4.setGravity(r7)
                                android.widget.TextView r4 = r0.confirmTextView
                                r7 = 1073741824(0x40000000, float:2.0)
                                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r10 = (float) r10
                                r11 = 1065353216(0x3f800000, float:1.0)
                                r4.setLineSpacing(r10, r11)
                                android.widget.TextView r4 = r0.confirmTextView
                                r10 = 2131691872(0x7f0f0960, float:1.9012828E38)
                                java.lang.String r12 = "LoginPasswordText"
                                java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r10)
                                r4.setText(r10)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x0058
                                r10 = 5
                                goto L_0x0059
                            L_0x0058:
                                r10 = 3
                            L_0x0059:
                                r12 = -2
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                                r0.addView(r4, r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                                r4.<init>(r1)
                                r0.codeField = r4
                                java.lang.String r10 = "windowBackgroundWhiteBlackText"
                                int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
                                r4.setTextColor(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
                                r4.setCursorColor(r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r10 = 1101004800(0x41a00000, float:20.0)
                                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                                r4.setCursorSize(r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r10 = 1069547520(0x3fc00000, float:1.5)
                                r4.setCursorWidth(r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                java.lang.String r10 = "windowBackgroundWhiteHintText"
                                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
                                r4.setHintTextColor(r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r10 = 0
                                android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r1, r10)
                                r4.setBackgroundDrawable(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r13 = 2131691870(0x7f0f095e, float:1.9012824E38)
                                java.lang.String r14 = "LoginPassword"
                                java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
                                r4.setHint(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r13 = 268435461(0x10000005, float:2.5243564E-29)
                                r4.setImeOptions(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r13 = 1099956224(0x41900000, float:18.0)
                                r4.setTextSize(r3, r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r4.setMaxLines(r3)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r4.setPadding(r10, r10, r10, r10)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r13 = 129(0x81, float:1.81E-43)
                                r4.setInputType(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                android.text.method.PasswordTransformationMethod r13 = android.text.method.PasswordTransformationMethod.getInstance()
                                r4.setTransformationMethod(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
                                r4.setTypeface(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                boolean r13 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r13 == 0) goto L_0x00e7
                                r13 = 5
                                goto L_0x00e8
                            L_0x00e7:
                                r13 = 3
                            L_0x00e8:
                                r4.setGravity(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r13 = -1
                                r14 = 36
                                r15 = 1
                                r16 = 0
                                r17 = 20
                                r18 = 0
                                r19 = 0
                                android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                                r0.addView(r4, r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$F0fVh7mHXsJU9rzAQiQOGvad6Do r13 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$F0fVh7mHXsJU9rzAQiQOGvad6Do
                                r13.<init>()
                                r4.setOnEditorActionListener(r13)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.cancelButton = r4
                                boolean r13 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r13 == 0) goto L_0x0117
                                r13 = 5
                                goto L_0x0118
                            L_0x0117:
                                r13 = 3
                            L_0x0118:
                                r13 = r13 | 48
                                r4.setGravity(r13)
                                android.widget.TextView r4 = r0.cancelButton
                                java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                                int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
                                r4.setTextColor(r13)
                                android.widget.TextView r4 = r0.cancelButton
                                r13 = 2131691347(0x7f0f0753, float:1.9011763E38)
                                java.lang.String r14 = "ForgotPassword"
                                java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
                                r4.setText(r13)
                                android.widget.TextView r4 = r0.cancelButton
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.cancelButton
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r13 = (float) r13
                                r4.setLineSpacing(r13, r11)
                                android.widget.TextView r4 = r0.cancelButton
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                                r4.setPadding(r10, r13, r10, r10)
                                android.widget.TextView r4 = r0.cancelButton
                                r13 = -1
                                boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r14 == 0) goto L_0x0157
                                r14 = 5
                                goto L_0x0158
                            L_0x0157:
                                r14 = 3
                            L_0x0158:
                                r14 = r14 | 48
                                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r12, (int) r14)
                                r0.addView(r4, r12)
                                android.widget.TextView r4 = r0.cancelButton
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$bs1v7HfzKrB7_IXWA9q2aQStoDw r12 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$bs1v7HfzKrB7_IXWA9q2aQStoDw
                                r12.<init>()
                                r4.setOnClickListener(r12)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountButton = r4
                                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r12 == 0) goto L_0x0178
                                r12 = 5
                                goto L_0x0179
                            L_0x0178:
                                r12 = 3
                            L_0x0179:
                                r12 = r12 | 48
                                r4.setGravity(r12)
                                android.widget.TextView r4 = r0.resetAccountButton
                                java.lang.String r12 = "windowBackgroundWhiteRedText6"
                                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
                                r4.setTextColor(r12)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r12 = 8
                                r4.setVisibility(r12)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r13 = 2131693500(0x7f0f0fbc, float:1.901613E38)
                                java.lang.String r14 = "ResetMyAccount"
                                java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
                                r4.setText(r13)
                                android.widget.TextView r4 = r0.resetAccountButton
                                java.lang.String r13 = "fonts/rmedium.ttf"
                                android.graphics.Typeface r13 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
                                r4.setTypeface(r13)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.resetAccountButton
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r13 = (float) r13
                                r4.setLineSpacing(r13, r11)
                                android.widget.TextView r4 = r0.resetAccountButton
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                                r4.setPadding(r10, r13, r10, r10)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r13 = -2
                                r14 = -2
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x01cb
                                r10 = 5
                                goto L_0x01cc
                            L_0x01cb:
                                r10 = 3
                            L_0x01cc:
                                r15 = r10 | 48
                                r16 = 0
                                r17 = 34
                                r18 = 0
                                r19 = 0
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                                r0.addView(r4, r10)
                                android.widget.TextView r4 = r0.resetAccountButton
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$7Arl5baH77fFnukVCe_pDPFQUWs r10 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityPasswordView$7Arl5baH77fFnukVCe_pDPFQUWs
                                r10.<init>()
                                r4.setOnClickListener(r10)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountText = r4
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x01f4
                                r10 = 5
                                goto L_0x01f5
                            L_0x01f4:
                                r10 = 3
                            L_0x01f5:
                                r10 = r10 | 48
                                r4.setGravity(r10)
                                android.widget.TextView r4 = r0.resetAccountText
                                r4.setVisibility(r12)
                                android.widget.TextView r4 = r0.resetAccountText
                                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r5)
                                android.widget.TextView r4 = r0.resetAccountText
                                r5 = 2131693501(0x7f0f0fbd, float:1.9016132E38)
                                java.lang.String r10 = "ResetMyAccountText"
                                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r5)
                                r4.setText(r5)
                                android.widget.TextView r4 = r0.resetAccountText
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r3 = r0.resetAccountText
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r4 = (float) r4
                                r3.setLineSpacing(r4, r11)
                                android.widget.TextView r3 = r0.resetAccountText
                                r10 = -2
                                r11 = -2
                                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r4 == 0) goto L_0x022e
                                goto L_0x022f
                            L_0x022e:
                                r8 = 3
                            L_0x022f:
                                r12 = r8 | 48
                                r13 = 0
                                r14 = 7
                                r15 = 0
                                r16 = 14
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                                r0.addView(r3, r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivityPasswordView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ boolean lambda$new$0$HloginActivity$LoginActivityPasswordView(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$4$HloginActivity$LoginActivityPasswordView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null) {
                                if (this.has_recovery) {
                                    this.this$0.needShowProgress(0);
                                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            HloginActivity.LoginActivityPasswordView.this.lambda$null$3$HloginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                        }
                                    }, 10);
                                    return;
                                }
                                this.resetAccountText.setVisibility(0);
                                this.resetAccountButton.setVisibility(0);
                                AndroidUtilities.hideKeyboard(this.codeField);
                                this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                            }
                        }

                        public /* synthetic */ void lambda$null$3$HloginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error, response) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$2$HloginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$2$HloginActivity$LoginActivityPasswordView(TLRPC.TL_error error, TLObject response) {
                            String timeString;
                            this.this$0.needHideProgress(false);
                            if (error == null) {
                                TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(res) {
                                    private final /* synthetic */ TLRPC.TL_auth_passwordRecovery f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivityPasswordView.this.lambda$null$1$HloginActivity$LoginActivityPasswordView(this.f$1, dialogInterface, i);
                                    }
                                });
                                Dialog dialog = this.this$0.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else if (error.text.startsWith("FLOOD_WAIT")) {
                                int time = Utilities.parseInt(error.text).intValue();
                                if (time < 60) {
                                    timeString = LocaleController.formatPluralString("Seconds", time);
                                } else {
                                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                }
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }

                        public /* synthetic */ void lambda$null$1$HloginActivity$LoginActivityPasswordView(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
                            Bundle bundle = new Bundle();
                            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
                            this.this$0.setPage(7, true, bundle, false);
                        }

                        public /* synthetic */ void lambda$new$8$HloginActivity$LoginActivityPasswordView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivityPasswordView.this.lambda$null$7$HloginActivity$LoginActivityPasswordView(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                            }
                        }

                        public /* synthetic */ void lambda$null$7$HloginActivity$LoginActivityPasswordView(DialogInterface dialogInterface, int i) {
                            this.this$0.needShowProgress(0);
                            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
                            req.reason = "Forgot password";
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$6$HloginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                }
                            }, 10);
                        }

                        public /* synthetic */ void lambda$null$6$HloginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error) {
                                private final /* synthetic */ TLRPC.TL_error f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$5$HloginActivity$LoginActivityPasswordView(this.f$1);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$5$HloginActivity$LoginActivityPasswordView(TLRPC.TL_error error) {
                            this.this$0.needHideProgress(false);
                            if (error == null) {
                                Bundle params = new Bundle();
                                params.putString("phoneFormated", this.requestPhone);
                                params.putString("phoneHash", this.phoneHash);
                                params.putString("code", this.phoneCode);
                                this.this$0.setPage(5, true, params, false);
                            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
                            } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                                Bundle params2 = new Bundle();
                                params2.putString("phoneFormated", this.requestPhone);
                                params2.putString("phoneHash", this.phoneHash);
                                params2.putString("code", this.phoneCode);
                                params2.putInt("startTime", ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime());
                                params2.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                                this.this$0.setPage(8, true, params2, false);
                            } else {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }

                        public String getHeaderName() {
                            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
                        }

                        public void onCancelPressed() {
                            this.nextPressed = false;
                        }

                        public void setParams(Bundle params, boolean restore) {
                            if (params != null) {
                                boolean z = false;
                                if (params.isEmpty()) {
                                    this.resetAccountButton.setVisibility(0);
                                    this.resetAccountText.setVisibility(0);
                                    AndroidUtilities.hideKeyboard(this.codeField);
                                    return;
                                }
                                this.resetAccountButton.setVisibility(8);
                                this.resetAccountText.setVisibility(8);
                                this.codeField.setText("");
                                this.currentParams = params;
                                this.current_salt1 = Utilities.hexToBytes(params.getString("current_salt1"));
                                this.current_salt2 = Utilities.hexToBytes(this.currentParams.getString("current_salt2"));
                                this.current_p = Utilities.hexToBytes(this.currentParams.getString("current_p"));
                                this.current_g = this.currentParams.getInt("current_g");
                                this.current_srp_B = Utilities.hexToBytes(this.currentParams.getString("current_srp_B"));
                                this.current_srp_id = this.currentParams.getLong("current_srp_id");
                                this.passwordType = this.currentParams.getInt("passwordType");
                                this.hint = this.currentParams.getString(TrackReferenceTypeBox.TYPE1);
                                if (this.currentParams.getInt("has_recovery") == 1) {
                                    z = true;
                                }
                                this.has_recovery = z;
                                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                                this.requestPhone = params.getString("phoneFormated");
                                this.phoneHash = params.getString("phoneHash");
                                this.phoneCode = params.getString("code");
                                String str = this.hint;
                                if (str == null || str.length() <= 0) {
                                    this.codeField.setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                                } else {
                                    this.codeField.setHint(this.hint);
                                }
                            }
                        }

                        private void onPasscodeError(boolean clear) {
                            if (this.this$0.getParentActivity() != null) {
                                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                if (clear) {
                                    this.codeField.setText("");
                                }
                                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
                            }
                        }

                        public void onNextPressed() {
                            if (!this.nextPressed) {
                                String oldPassword = this.codeField.getText().toString();
                                if (oldPassword.length() == 0) {
                                    onPasscodeError(false);
                                    return;
                                }
                                this.nextPressed = true;
                                this.this$0.needShowProgress(0);
                                Utilities.globalQueue.postRunnable(new Runnable(oldPassword) {
                                    private final /* synthetic */ String f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        HloginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$13$HloginActivity$LoginActivityPasswordView(this.f$1);
                                    }
                                });
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$13$HloginActivity$LoginActivityPasswordView(String oldPassword) {
                            byte[] passwordBytes;
                            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = null;
                            if (this.passwordType == 1) {
                                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = new TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow();
                                algo2.salt1 = this.current_salt1;
                                algo2.salt2 = this.current_salt2;
                                algo2.g = this.current_g;
                                algo2.p = this.current_p;
                                algo = algo2;
                            }
                            if (algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                                passwordBytes = SRPHelper.getX(AndroidUtilities.getStringBytes(oldPassword), algo);
                            } else {
                                passwordBytes = null;
                            }
                            TLRPC.TL_auth_checkPassword req = new TLRPC.TL_auth_checkPassword();
                            RequestDelegate requestDelegate = new RequestDelegate() {
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$12$HloginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                }
                            };
                            if (algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo3 = algo;
                                algo3.salt1 = this.current_salt1;
                                algo3.salt2 = this.current_salt2;
                                algo3.g = this.current_g;
                                algo3.p = this.current_p;
                                req.password = SRPHelper.startCheck(passwordBytes, this.current_srp_id, this.current_srp_B, algo3);
                                if (req.password == null) {
                                    TLRPC.TL_error error = new TLRPC.TL_error();
                                    error.text = "PASSWORD_HASH_INVALID";
                                    requestDelegate.run((TLObject) null, error);
                                    return;
                                }
                                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, requestDelegate, 10);
                            }
                        }

                        public /* synthetic */ void lambda$null$12$HloginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error, response) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$11$HloginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$11$HloginActivity$LoginActivityPasswordView(TLRPC.TL_error error, TLObject response) {
                            String timeString;
                            this.nextPressed = false;
                            if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                                this.this$0.needHideProgress(false);
                                if (response instanceof TLRPC.TL_auth_authorization) {
                                    this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                                } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                                    onPasscodeError(true);
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    int time = Utilities.parseInt(error.text).intValue();
                                    if (time < 60) {
                                        timeString = LocaleController.formatPluralString("Seconds", time);
                                    } else {
                                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                    }
                                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            } else {
                                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        HloginActivity.LoginActivityPasswordView.this.lambda$null$10$HloginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                    }
                                }, 8);
                            }
                        }

                        public /* synthetic */ void lambda$null$10$HloginActivity$LoginActivityPasswordView(TLObject response2, TLRPC.TL_error error2) {
                            AndroidUtilities.runOnUIThread(new Runnable(error2, response2) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$null$9$HloginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$9$HloginActivity$LoginActivityPasswordView(TLRPC.TL_error error2, TLObject response2) {
                            if (error2 == null) {
                                TLRPC.TL_account_password password = (TLRPC.TL_account_password) response2;
                                this.current_srp_B = password.srp_B;
                                this.current_srp_id = password.srp_id;
                                onNextPressed();
                            }
                        }

                        public boolean needBackButton() {
                            return true;
                        }

                        public boolean onBackPressed(boolean force) {
                            this.nextPressed = false;
                            this.this$0.needHideProgress(true);
                            this.currentParams = null;
                            return true;
                        }

                        public void onShow() {
                            super.onShow();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    HloginActivity.LoginActivityPasswordView.this.lambda$onShow$14$HloginActivity$LoginActivityPasswordView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$14$HloginActivity$LoginActivityPasswordView() {
                            EditTextBoldCursor editTextBoldCursor = this.codeField;
                            if (editTextBoldCursor != null) {
                                editTextBoldCursor.requestFocus();
                                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                                AndroidUtilities.showKeyboard(this.codeField);
                            }
                        }

                        public void saveStateParams(Bundle bundle) {
                            String code = this.codeField.getText().toString();
                            if (code.length() != 0) {
                                bundle.putString("passview_code", code);
                            }
                            Bundle bundle2 = this.currentParams;
                            if (bundle2 != null) {
                                bundle.putBundle("passview_params", bundle2);
                            }
                        }

                        public void restoreStateParams(Bundle bundle) {
                            Bundle bundle2 = bundle.getBundle("passview_params");
                            this.currentParams = bundle2;
                            if (bundle2 != null) {
                                setParams(bundle2, true);
                            }
                            String code = bundle.getString("passview_code");
                            if (code != null) {
                                this.codeField.setText(code);
                            }
                        }
                    }

                    public class LoginActivityResetWaitView extends SlideView {
                        /* access modifiers changed from: private */
                        public TextView confirmTextView;
                        private Bundle currentParams;
                        private String phoneCode;
                        private String phoneHash;
                        private String requestPhone;
                        /* access modifiers changed from: private */
                        public TextView resetAccountButton;
                        /* access modifiers changed from: private */
                        public TextView resetAccountText;
                        /* access modifiers changed from: private */
                        public TextView resetAccountTime;
                        private int startTime;
                        final /* synthetic */ HloginActivity this$0;
                        /* access modifiers changed from: private */
                        public Runnable timeRunnable;
                        private int waitTime;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityResetWaitView(im.bclpbkiauv.ui.hui.login.HloginActivity r20, android.content.Context r21) {
                            /*
                                r19 = this;
                                r0 = r19
                                r1 = r21
                                r2 = r20
                                r0.this$0 = r2
                                r0.<init>(r1)
                                r3 = 1
                                r0.setOrientation(r3)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.confirmTextView = r4
                                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r6)
                                android.widget.TextView r4 = r0.confirmTextView
                                r6 = 1096810496(0x41600000, float:14.0)
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r8 = 5
                                r9 = 3
                                if (r7 == 0) goto L_0x0030
                                r7 = 5
                                goto L_0x0031
                            L_0x0030:
                                r7 = 3
                            L_0x0031:
                                r4.setGravity(r7)
                                android.widget.TextView r4 = r0.confirmTextView
                                r7 = 1073741824(0x40000000, float:2.0)
                                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r10 = (float) r10
                                r11 = 1065353216(0x3f800000, float:1.0)
                                r4.setLineSpacing(r10, r11)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x004a
                                r10 = 5
                                goto L_0x004b
                            L_0x004a:
                                r10 = 3
                            L_0x004b:
                                r12 = -2
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                                r0.addView(r4, r10)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountText = r4
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x0060
                                r10 = 5
                                goto L_0x0061
                            L_0x0060:
                                r10 = 3
                            L_0x0061:
                                r10 = r10 | 48
                                r4.setGravity(r10)
                                android.widget.TextView r4 = r0.resetAccountText
                                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r10)
                                android.widget.TextView r4 = r0.resetAccountText
                                r10 = 2131693491(0x7f0f0fb3, float:1.9016112E38)
                                java.lang.String r12 = "ResetAccountStatus"
                                java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r10)
                                r4.setText(r10)
                                android.widget.TextView r4 = r0.resetAccountText
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.resetAccountText
                                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r10 = (float) r10
                                r4.setLineSpacing(r10, r11)
                                android.widget.TextView r4 = r0.resetAccountText
                                r12 = -2
                                r13 = -2
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x0096
                                r10 = 5
                                goto L_0x0097
                            L_0x0096:
                                r10 = 3
                            L_0x0097:
                                r14 = r10 | 48
                                r15 = 0
                                r16 = 24
                                r17 = 0
                                r18 = 0
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                                r0.addView(r4, r10)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountTime = r4
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x00b4
                                r10 = 5
                                goto L_0x00b5
                            L_0x00b4:
                                r10 = 3
                            L_0x00b5:
                                r10 = r10 | 48
                                r4.setGravity(r10)
                                android.widget.TextView r4 = r0.resetAccountTime
                                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r5)
                                android.widget.TextView r4 = r0.resetAccountTime
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r4 = r0.resetAccountTime
                                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r5 = (float) r5
                                r4.setLineSpacing(r5, r11)
                                android.widget.TextView r4 = r0.resetAccountTime
                                r12 = -2
                                r13 = -2
                                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r5 == 0) goto L_0x00dc
                                r5 = 5
                                goto L_0x00dd
                            L_0x00dc:
                                r5 = 3
                            L_0x00dd:
                                r14 = r5 | 48
                                r15 = 0
                                r16 = 2
                                r17 = 0
                                r18 = 0
                                android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                                r0.addView(r4, r5)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountButton = r4
                                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r5 == 0) goto L_0x00fa
                                r5 = 5
                                goto L_0x00fb
                            L_0x00fa:
                                r5 = 3
                            L_0x00fb:
                                r5 = r5 | 48
                                r4.setGravity(r5)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r5 = 2131693487(0x7f0f0faf, float:1.9016104E38)
                                java.lang.String r10 = "ResetAccountButton"
                                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r5)
                                r4.setText(r5)
                                android.widget.TextView r4 = r0.resetAccountButton
                                java.lang.String r5 = "fonts/rmedium.ttf"
                                android.graphics.Typeface r5 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r5)
                                r4.setTypeface(r5)
                                android.widget.TextView r4 = r0.resetAccountButton
                                r4.setTextSize(r3, r6)
                                android.widget.TextView r3 = r0.resetAccountButton
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r4 = (float) r4
                                r3.setLineSpacing(r4, r11)
                                android.widget.TextView r3 = r0.resetAccountButton
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                                r5 = 0
                                r3.setPadding(r5, r4, r5, r5)
                                android.widget.TextView r3 = r0.resetAccountButton
                                r10 = -2
                                r11 = -2
                                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r4 == 0) goto L_0x013b
                                goto L_0x013c
                            L_0x013b:
                                r8 = 3
                            L_0x013c:
                                r12 = r8 | 48
                                r13 = 0
                                r14 = 7
                                r15 = 0
                                r16 = 0
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                                r0.addView(r3, r4)
                                android.widget.TextView r3 = r0.resetAccountButton
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityResetWaitView$CCH7-eUc-A1TIwJWiMrAsHPZivk r4 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityResetWaitView$CCH7-eUc-A1TIwJWiMrAsHPZivk
                                r4.<init>()
                                r3.setOnClickListener(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivityResetWaitView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ void lambda$new$3$HloginActivity$LoginActivityResetWaitView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivityResetWaitView.this.lambda$null$2$HloginActivity$LoginActivityResetWaitView(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                            }
                        }

                        public /* synthetic */ void lambda$null$2$HloginActivity$LoginActivityResetWaitView(DialogInterface dialogInterface, int i) {
                            this.this$0.needShowProgress(0);
                            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
                            req.reason = "Forgot password";
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    HloginActivity.LoginActivityResetWaitView.this.lambda$null$1$HloginActivity$LoginActivityResetWaitView(tLObject, tL_error);
                                }
                            }, 10);
                        }

                        public /* synthetic */ void lambda$null$1$HloginActivity$LoginActivityResetWaitView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error) {
                                private final /* synthetic */ TLRPC.TL_error f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityResetWaitView.this.lambda$null$0$HloginActivity$LoginActivityResetWaitView(this.f$1);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$0$HloginActivity$LoginActivityResetWaitView(TLRPC.TL_error error) {
                            this.this$0.needHideProgress(false);
                            if (error == null) {
                                Bundle params = new Bundle();
                                params.putString("phoneFormated", this.requestPhone);
                                params.putString("phoneHash", this.phoneHash);
                                params.putString("code", this.phoneCode);
                                this.this$0.setPage(5, true, params, false);
                            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
                            } else {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }

                        public String getHeaderName() {
                            return LocaleController.getString("ResetAccount", R.string.ResetAccount);
                        }

                        /* access modifiers changed from: private */
                        public void updateTimeText() {
                            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
                            int days = timeLeft / 86400;
                            int hours = (timeLeft - (days * 86400)) / 3600;
                            int minutes = ((timeLeft - (86400 * days)) - (hours * 3600)) / 60;
                            int seconds = timeLeft % 60;
                            if (days != 0) {
                                TextView textView = this.resetAccountTime;
                                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", days) + " " + LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes)));
                            } else {
                                TextView textView2 = this.resetAccountTime;
                                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes) + " " + LocaleController.formatPluralString("SecondsBold", seconds)));
                            }
                            if (timeLeft > 0) {
                                this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteGrayText6);
                                this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
                                return;
                            }
                            this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteRedText6);
                            this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText6));
                        }

                        public void setParams(Bundle params, boolean restore) {
                            if (params != null) {
                                this.currentParams = params;
                                this.requestPhone = params.getString("phoneFormated");
                                this.phoneHash = params.getString("phoneHash");
                                this.phoneCode = params.getString("code");
                                this.startTime = params.getInt("startTime");
                                this.waitTime = params.getInt("waitTime");
                                TextView textView = this.confirmTextView;
                                PhoneFormat instance = PhoneFormat.getInstance();
                                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", R.string.ResetAccountInfo, LocaleController.addNbsp(instance.format(Marker.ANY_NON_NULL_MARKER + this.requestPhone)))));
                                updateTimeText();
                                AnonymousClass1 r0 = new Runnable() {
                                    public void run() {
                                        if (LoginActivityResetWaitView.this.timeRunnable == this) {
                                            LoginActivityResetWaitView.this.updateTimeText();
                                            AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                                        }
                                    }
                                };
                                this.timeRunnable = r0;
                                AndroidUtilities.runOnUIThread(r0, 1000);
                            }
                        }

                        public boolean needBackButton() {
                            return true;
                        }

                        public boolean onBackPressed(boolean force) {
                            this.this$0.needHideProgress(true);
                            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
                            this.timeRunnable = null;
                            this.currentParams = null;
                            return true;
                        }

                        public void saveStateParams(Bundle bundle) {
                            Bundle bundle2 = this.currentParams;
                            if (bundle2 != null) {
                                bundle.putBundle("resetview_params", bundle2);
                            }
                        }

                        public void restoreStateParams(Bundle bundle) {
                            Bundle bundle2 = bundle.getBundle("resetview_params");
                            this.currentParams = bundle2;
                            if (bundle2 != null) {
                                setParams(bundle2, true);
                            }
                        }
                    }

                    public class LoginActivityRecoverView extends SlideView {
                        /* access modifiers changed from: private */
                        public TextView cancelButton;
                        /* access modifiers changed from: private */
                        public EditTextBoldCursor codeField;
                        /* access modifiers changed from: private */
                        public TextView confirmTextView;
                        private Bundle currentParams;
                        private String email_unconfirmed_pattern;
                        private boolean nextPressed;
                        final /* synthetic */ HloginActivity this$0;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityRecoverView(im.bclpbkiauv.ui.hui.login.HloginActivity r19, android.content.Context r20) {
                            /*
                                r18 = this;
                                r0 = r18
                                r1 = r20
                                r2 = r19
                                r0.this$0 = r2
                                r0.<init>(r1)
                                r3 = 1
                                r0.setOrientation(r3)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.confirmTextView = r4
                                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                                r4.setTextColor(r5)
                                android.widget.TextView r4 = r0.confirmTextView
                                r5 = 1096810496(0x41600000, float:14.0)
                                r4.setTextSize(r3, r5)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r7 = 5
                                r8 = 3
                                if (r6 == 0) goto L_0x0030
                                r6 = 5
                                goto L_0x0031
                            L_0x0030:
                                r6 = 3
                            L_0x0031:
                                r4.setGravity(r6)
                                android.widget.TextView r4 = r0.confirmTextView
                                r6 = 1073741824(0x40000000, float:2.0)
                                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                                float r9 = (float) r9
                                r10 = 1065353216(0x3f800000, float:1.0)
                                r4.setLineSpacing(r9, r10)
                                android.widget.TextView r4 = r0.confirmTextView
                                r9 = 2131693514(0x7f0f0fca, float:1.9016158E38)
                                java.lang.String r11 = "RestoreEmailSentInfo"
                                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
                                r4.setText(r9)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r9 == 0) goto L_0x0058
                                r9 = 5
                                goto L_0x0059
                            L_0x0058:
                                r9 = 3
                            L_0x0059:
                                r11 = -2
                                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r9)
                                r0.addView(r4, r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                                r4.<init>(r1)
                                r0.codeField = r4
                                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                                r4.setTextColor(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                                r4.setCursorColor(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r9 = 1101004800(0x41a00000, float:20.0)
                                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                                r4.setCursorSize(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r9 = 1069547520(0x3fc00000, float:1.5)
                                r4.setCursorWidth(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                java.lang.String r9 = "windowBackgroundWhiteHintText"
                                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                                r4.setHintTextColor(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r9 = 0
                                android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r1, r9)
                                r4.setBackgroundDrawable(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r11 = 2131692790(0x7f0f0cf6, float:1.901469E38)
                                java.lang.String r12 = "PasswordCode"
                                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
                                r4.setHint(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r11 = 268435461(0x10000005, float:2.5243564E-29)
                                r4.setImeOptions(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r11 = 1099956224(0x41900000, float:18.0)
                                r4.setTextSize(r3, r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r4.setMaxLines(r3)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r4.setPadding(r9, r9, r9, r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r4.setInputType(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                android.text.method.PasswordTransformationMethod r11 = android.text.method.PasswordTransformationMethod.getInstance()
                                r4.setTransformationMethod(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
                                r4.setTypeface(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r11 == 0) goto L_0x00e5
                                r11 = 5
                                goto L_0x00e6
                            L_0x00e5:
                                r11 = 3
                            L_0x00e6:
                                r4.setGravity(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                r11 = -1
                                r12 = 36
                                r13 = 1
                                r14 = 0
                                r15 = 20
                                r16 = 0
                                r17 = 0
                                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                                r0.addView(r4, r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.codeField
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRecoverView$nZsEM2tR7zq3tw11g2R-LWaV-OQ r11 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRecoverView$nZsEM2tR7zq3tw11g2R-LWaV-OQ
                                r11.<init>()
                                r4.setOnEditorActionListener(r11)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.cancelButton = r4
                                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r11 == 0) goto L_0x0114
                                r11 = 5
                                goto L_0x0115
                            L_0x0114:
                                r11 = 3
                            L_0x0115:
                                r11 = r11 | 80
                                r4.setGravity(r11)
                                android.widget.TextView r4 = r0.cancelButton
                                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
                                r4.setTextColor(r11)
                                android.widget.TextView r4 = r0.cancelButton
                                r4.setTextSize(r3, r5)
                                android.widget.TextView r3 = r0.cancelButton
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                                float r4 = (float) r4
                                r3.setLineSpacing(r4, r10)
                                android.widget.TextView r3 = r0.cancelButton
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                                r3.setPadding(r9, r4, r9, r9)
                                android.widget.TextView r3 = r0.cancelButton
                                r9 = -2
                                r10 = -2
                                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r4 == 0) goto L_0x0146
                                goto L_0x0147
                            L_0x0146:
                                r7 = 3
                            L_0x0147:
                                r11 = r7 | 80
                                r12 = 0
                                r13 = 0
                                r14 = 0
                                r15 = 14
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                                r0.addView(r3, r4)
                                android.widget.TextView r3 = r0.cancelButton
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRecoverView$fE0wu6FMxVKxP-66pxKUlRWFljs r4 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRecoverView$fE0wu6FMxVKxP-66pxKUlRWFljs
                                r4.<init>()
                                r3.setOnClickListener(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivityRecoverView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ boolean lambda$new$0$HloginActivity$LoginActivityRecoverView(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$2$HloginActivity$LoginActivityRecoverView(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
                            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    HloginActivity.LoginActivityRecoverView.this.lambda$null$1$HloginActivity$LoginActivityRecoverView(dialogInterface, i);
                                }
                            });
                            Dialog dialog = this.this$0.showDialog(builder.create());
                            if (dialog != null) {
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                            }
                        }

                        public /* synthetic */ void lambda$null$1$HloginActivity$LoginActivityRecoverView(DialogInterface dialogInterface, int i) {
                            this.this$0.setPage(6, true, new Bundle(), true);
                        }

                        public boolean needBackButton() {
                            return true;
                        }

                        public void onCancelPressed() {
                            this.nextPressed = false;
                        }

                        public String getHeaderName() {
                            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
                        }

                        public void setParams(Bundle params, boolean restore) {
                            if (params != null) {
                                this.codeField.setText("");
                                this.currentParams = params;
                                String string = params.getString("email_unconfirmed_pattern");
                                this.email_unconfirmed_pattern = string;
                                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", R.string.RestoreEmailTrouble, string));
                                AndroidUtilities.showKeyboard(this.codeField);
                                this.codeField.requestFocus();
                            }
                        }

                        private void onPasscodeError(boolean clear) {
                            if (this.this$0.getParentActivity() != null) {
                                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                if (clear) {
                                    this.codeField.setText("");
                                }
                                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
                            }
                        }

                        public void onNextPressed() {
                            if (!this.nextPressed) {
                                if (this.codeField.getText().toString().length() == 0) {
                                    onPasscodeError(false);
                                    return;
                                }
                                this.nextPressed = true;
                                String code = this.codeField.getText().toString();
                                if (code.length() == 0) {
                                    onPasscodeError(false);
                                    return;
                                }
                                this.this$0.needShowProgress(0);
                                TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
                                req.code = code;
                                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        HloginActivity.LoginActivityRecoverView.this.lambda$onNextPressed$5$HloginActivity$LoginActivityRecoverView(tLObject, tL_error);
                                    }
                                }, 10);
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$5$HloginActivity$LoginActivityRecoverView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(response, error) {
                                private final /* synthetic */ TLObject f$1;
                                private final /* synthetic */ TLRPC.TL_error f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityRecoverView.this.lambda$null$4$HloginActivity$LoginActivityRecoverView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$4$HloginActivity$LoginActivityRecoverView(TLObject response, TLRPC.TL_error error) {
                            String timeString;
                            this.this$0.needHideProgress(false);
                            this.nextPressed = false;
                            if (response instanceof TLRPC.TL_auth_authorization) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(response) {
                                    private final /* synthetic */ TLObject f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivityRecoverView.this.lambda$null$3$HloginActivity$LoginActivityRecoverView(this.f$1, dialogInterface, i);
                                    }
                                });
                                builder.setMessage(LocaleController.getString("PasswordReset", R.string.PasswordReset));
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                Dialog dialog = this.this$0.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else if (error.text.startsWith("CODE_INVALID")) {
                                onPasscodeError(true);
                            } else if (error.text.startsWith("FLOOD_WAIT")) {
                                int time = Utilities.parseInt(error.text).intValue();
                                if (time < 60) {
                                    timeString = LocaleController.formatPluralString("Seconds", time);
                                } else {
                                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                }
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }

                        public /* synthetic */ void lambda$null$3$HloginActivity$LoginActivityRecoverView(TLObject response, DialogInterface dialogInterface, int i) {
                            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                        }

                        public boolean onBackPressed(boolean force) {
                            this.this$0.needHideProgress(true);
                            this.currentParams = null;
                            this.nextPressed = false;
                            return true;
                        }

                        public void onShow() {
                            super.onShow();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    HloginActivity.LoginActivityRecoverView.this.lambda$onShow$6$HloginActivity$LoginActivityRecoverView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$6$HloginActivity$LoginActivityRecoverView() {
                            EditTextBoldCursor editTextBoldCursor = this.codeField;
                            if (editTextBoldCursor != null) {
                                editTextBoldCursor.requestFocus();
                                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                            }
                        }

                        public void saveStateParams(Bundle bundle) {
                            String code = this.codeField.getText().toString();
                            if (!(code == null || code.length() == 0)) {
                                bundle.putString("recoveryview_code", code);
                            }
                            Bundle bundle2 = this.currentParams;
                            if (bundle2 != null) {
                                bundle.putBundle("recoveryview_params", bundle2);
                            }
                        }

                        public void restoreStateParams(Bundle bundle) {
                            Bundle bundle2 = bundle.getBundle("recoveryview_params");
                            this.currentParams = bundle2;
                            if (bundle2 != null) {
                                setParams(bundle2, true);
                            }
                            String code = bundle.getString("recoveryview_code");
                            if (code != null) {
                                this.codeField.setText(code);
                            }
                        }
                    }

                    public class LoginActivityRegisterView extends SlideView implements ImageUpdater.ImageUpdaterDelegate {
                        private TLRPC.FileLocation avatar;
                        /* access modifiers changed from: private */
                        public AnimatorSet avatarAnimation;
                        private TLRPC.FileLocation avatarBig;
                        private AvatarDrawable avatarDrawable;
                        /* access modifiers changed from: private */
                        public ImageView avatarEditor;
                        /* access modifiers changed from: private */
                        public BackupImageView avatarImage;
                        /* access modifiers changed from: private */
                        public View avatarOverlay;
                        /* access modifiers changed from: private */
                        public RadialProgressView avatarProgressView;
                        private boolean createAfterUpload;
                        private Bundle currentParams;
                        /* access modifiers changed from: private */
                        public EditTextBoldCursor firstNameField;
                        /* access modifiers changed from: private */
                        public ImageUpdater imageUpdater;
                        /* access modifiers changed from: private */
                        public EditTextBoldCursor lastNameField;
                        private boolean nextPressed = false;
                        private String phoneCode;
                        private String phoneHash;
                        /* access modifiers changed from: private */
                        public TextView privacyView;
                        private String requestPhone;
                        /* access modifiers changed from: private */
                        public TextView textView;
                        final /* synthetic */ HloginActivity this$0;
                        private TLRPC.InputFile uploadedAvatar;
                        /* access modifiers changed from: private */
                        public TextView wrongNumber;

                        public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                            ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
                        }

                        public /* synthetic */ String getInitialSearchString() {
                            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
                        }

                        public class LinkSpan extends ClickableSpan {
                            public LinkSpan() {
                            }

                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                            }

                            public void onClick(View widget) {
                                LoginActivityRegisterView.this.showTermsOfService(false);
                            }
                        }

                        /* access modifiers changed from: private */
                        public void showTermsOfService(boolean needAccept) {
                            if (this.this$0.currentTermsOfService != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
                                if (needAccept) {
                                    builder.setPositiveButton(LocaleController.getString("Accept", R.string.Accept), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            HloginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$0$HloginActivity$LoginActivityRegisterView(dialogInterface, i);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            HloginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$3$HloginActivity$LoginActivityRegisterView(dialogInterface, i);
                                        }
                                    });
                                } else {
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                }
                                SpannableStringBuilder text = new SpannableStringBuilder(this.this$0.currentTermsOfService.text);
                                MessageObject.addEntitiesToText(text, this.this$0.currentTermsOfService.entities, false, 0, false, false, false);
                                builder.setMessage(text);
                                this.this$0.showDialog(builder.create());
                            }
                        }

                        public /* synthetic */ void lambda$showTermsOfService$0$HloginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
                            this.this$0.currentTermsOfService.popup = false;
                            onNextPressed();
                        }

                        public /* synthetic */ void lambda$showTermsOfService$3$HloginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder1.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
                            builder1.setMessage(LocaleController.getString("TosDecline", R.string.TosDecline));
                            builder1.setPositiveButton(LocaleController.getString("SignUp", R.string.SignUp), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$null$1$HloginActivity$LoginActivityRegisterView(dialogInterface, i);
                                }
                            });
                            builder1.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$null$2$HloginActivity$LoginActivityRegisterView(dialogInterface, i);
                                }
                            });
                            this.this$0.showDialog(builder1.create());
                        }

                        public /* synthetic */ void lambda$null$1$HloginActivity$LoginActivityRegisterView(DialogInterface dialog1, int which1) {
                            this.this$0.currentTermsOfService.popup = false;
                            onNextPressed();
                        }

                        public /* synthetic */ void lambda$null$2$HloginActivity$LoginActivityRegisterView(DialogInterface dialog12, int which12) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityRegisterView(im.bclpbkiauv.ui.hui.login.HloginActivity r30, android.content.Context r31) {
                            /*
                                r29 = this;
                                r0 = r29
                                r1 = r30
                                r2 = r31
                                r0.this$0 = r1
                                r0.<init>(r2)
                                r3 = 0
                                r0.nextPressed = r3
                                r4 = 1
                                r0.setOrientation(r4)
                                im.bclpbkiauv.ui.components.ImageUpdater r5 = new im.bclpbkiauv.ui.components.ImageUpdater
                                r5.<init>()
                                r0.imageUpdater = r5
                                r5.setSearchAvailable(r3)
                                im.bclpbkiauv.ui.components.ImageUpdater r5 = r0.imageUpdater
                                r5.setUploadAfterSelect(r3)
                                im.bclpbkiauv.ui.components.ImageUpdater r5 = r0.imageUpdater
                                r5.parentFragment = r1
                                im.bclpbkiauv.ui.components.ImageUpdater r5 = r0.imageUpdater
                                r5.delegate = r0
                                android.widget.TextView r5 = new android.widget.TextView
                                r5.<init>(r2)
                                r0.textView = r5
                                java.lang.String r6 = "RegisterText2"
                                r7 = 2131693407(0x7f0f0f5f, float:1.9015941E38)
                                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r7)
                                r5.setText(r6)
                                android.widget.TextView r5 = r0.textView
                                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                                r5.setTextColor(r7)
                                android.widget.TextView r5 = r0.textView
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r9 = 5
                                if (r7 == 0) goto L_0x0050
                                r7 = 5
                                goto L_0x0051
                            L_0x0050:
                                r7 = 3
                            L_0x0051:
                                r5.setGravity(r7)
                                android.widget.TextView r5 = r0.textView
                                r7 = 1096810496(0x41600000, float:14.0)
                                r5.setTextSize(r4, r7)
                                android.widget.TextView r5 = r0.textView
                                r10 = -2
                                r11 = -2
                                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r12 == 0) goto L_0x0065
                                r12 = 5
                                goto L_0x0066
                            L_0x0065:
                                r12 = 3
                            L_0x0066:
                                r13 = 0
                                r14 = 0
                                r15 = 0
                                r16 = 0
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                                r0.addView(r5, r10)
                                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                                r5.<init>(r2)
                                r10 = -1
                                r11 = -2
                                r12 = 0
                                r13 = 1101529088(0x41a80000, float:21.0)
                                r14 = 0
                                r15 = 0
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (float) r12, (float) r13, (float) r14, (float) r15)
                                r0.addView(r5, r10)
                                im.bclpbkiauv.ui.components.AvatarDrawable r10 = new im.bclpbkiauv.ui.components.AvatarDrawable
                                r10.<init>()
                                r0.avatarDrawable = r10
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$1 r10 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$1
                                r10.<init>(r2, r1)
                                r0.avatarImage = r10
                                r11 = 1107296256(0x42000000, float:32.0)
                                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                                r10.setRoundRadius(r11)
                                im.bclpbkiauv.ui.components.AvatarDrawable r10 = r0.avatarDrawable
                                r11 = 0
                                r10.setInfo(r9, r11, r11)
                                im.bclpbkiauv.ui.components.BackupImageView r10 = r0.avatarImage
                                im.bclpbkiauv.ui.components.AvatarDrawable r11 = r0.avatarDrawable
                                r10.setImageDrawable(r11)
                                im.bclpbkiauv.ui.components.BackupImageView r10 = r0.avatarImage
                                r11 = 1115684864(0x42800000, float:64.0)
                                r12 = 1115684864(0x42800000, float:64.0)
                                boolean r13 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r13 == 0) goto L_0x00b5
                                r13 = 5
                                goto L_0x00b6
                            L_0x00b5:
                                r13 = 3
                            L_0x00b6:
                                r13 = r13 | 48
                                r14 = 0
                                r15 = 1098907648(0x41800000, float:16.0)
                                r16 = 0
                                r17 = 0
                                android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                                r5.addView(r10, r11)
                                android.graphics.Paint r10 = new android.graphics.Paint
                                r10.<init>(r4)
                                r11 = 1426063360(0x55000000, float:8.796093E12)
                                r10.setColor(r11)
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$2 r11 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$2
                                r11.<init>(r2, r1, r10)
                                r0.avatarOverlay = r11
                                r12 = 1115684864(0x42800000, float:64.0)
                                r13 = 1115684864(0x42800000, float:64.0)
                                boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r14 == 0) goto L_0x00e1
                                r14 = 5
                                goto L_0x00e2
                            L_0x00e1:
                                r14 = 3
                            L_0x00e2:
                                r14 = r14 | 48
                                r15 = 0
                                r16 = 1098907648(0x41800000, float:16.0)
                                r17 = 0
                                r18 = 0
                                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                                r5.addView(r11, r12)
                                android.view.View r11 = r0.avatarOverlay
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$g_zhsW08RtEqPEhxSrx6z6GWaj4 r12 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$g_zhsW08RtEqPEhxSrx6z6GWaj4
                                r12.<init>()
                                r11.setOnClickListener(r12)
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$3 r11 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$3
                                r11.<init>(r2, r1)
                                r0.avatarEditor = r11
                                android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
                                r11.setScaleType(r12)
                                android.widget.ImageView r11 = r0.avatarEditor
                                r12 = 2131230820(0x7f080064, float:1.8077704E38)
                                r11.setImageResource(r12)
                                android.widget.ImageView r11 = r0.avatarEditor
                                r11.setEnabled(r3)
                                android.widget.ImageView r11 = r0.avatarEditor
                                r11.setClickable(r3)
                                android.widget.ImageView r11 = r0.avatarEditor
                                r12 = 1073741824(0x40000000, float:2.0)
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                                r11.setPadding(r13, r3, r3, r3)
                                android.widget.ImageView r11 = r0.avatarEditor
                                r13 = 1115684864(0x42800000, float:64.0)
                                r14 = 1115684864(0x42800000, float:64.0)
                                boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r15 == 0) goto L_0x0131
                                r15 = 5
                                goto L_0x0132
                            L_0x0131:
                                r15 = 3
                            L_0x0132:
                                r15 = r15 | 48
                                r16 = 0
                                r17 = 1098907648(0x41800000, float:16.0)
                                r18 = 0
                                r19 = 0
                                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                                r5.addView(r11, r13)
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$4 r11 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$4
                                r11.<init>(r2, r1)
                                r0.avatarProgressView = r11
                                r13 = 1106247680(0x41f00000, float:30.0)
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
                                r11.setSize(r13)
                                im.bclpbkiauv.ui.components.RadialProgressView r11 = r0.avatarProgressView
                                r13 = -1
                                r11.setProgressColor(r13)
                                im.bclpbkiauv.ui.components.RadialProgressView r11 = r0.avatarProgressView
                                r14 = 1115684864(0x42800000, float:64.0)
                                r15 = 1115684864(0x42800000, float:64.0)
                                boolean r16 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r16 == 0) goto L_0x0166
                                r16 = 5
                                goto L_0x0168
                            L_0x0166:
                                r16 = 3
                            L_0x0168:
                                r16 = r16 | 48
                                r17 = 0
                                r18 = 1098907648(0x41800000, float:16.0)
                                r19 = 0
                                r20 = 0
                                android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                                r5.addView(r11, r14)
                                r0.showAvatarProgress(r3, r3)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                                r11.<init>(r2)
                                r0.firstNameField = r11
                                java.lang.String r14 = "windowBackgroundWhiteHintText"
                                int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                                r11.setHintTextColor(r15)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r0.firstNameField
                                java.lang.String r15 = "windowBackgroundWhiteBlackText"
                                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                                r11.setTextColor(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r2, r3)
                                r8.setBackgroundDrawable(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                                r8.setCursorColor(r11)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r11 = 1101004800(0x41a00000, float:20.0)
                                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                                r8.setCursorSize(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r9 = 1069547520(0x3fc00000, float:1.5)
                                r8.setCursorWidth(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r13 = 2131691320(0x7f0f0738, float:1.9011709E38)
                                java.lang.String r12 = "FirstName"
                                java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r13)
                                r8.setHint(r12)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r12 = 268435461(0x10000005, float:2.5243564E-29)
                                r8.setImeOptions(r12)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r12 = 1099431936(0x41880000, float:17.0)
                                r8.setTextSize(r4, r12)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r8.setMaxLines(r4)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r13 = 8192(0x2000, float:1.14794E-41)
                                r8.setInputType(r13)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r8 = r0.firstNameField
                                r20 = -1082130432(0xffffffffbf800000, float:-1.0)
                                r21 = 1108344832(0x42100000, float:36.0)
                                boolean r22 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r22 == 0) goto L_0x01f1
                                r22 = 5
                                goto L_0x01f3
                            L_0x01f1:
                                r22 = 3
                            L_0x01f3:
                                r22 = r22 | 48
                                boolean r23 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r27 = 0
                                r28 = 1118437376(0x42aa0000, float:85.0)
                                if (r23 == 0) goto L_0x0200
                                r23 = 0
                                goto L_0x0202
                            L_0x0200:
                                r23 = 1118437376(0x42aa0000, float:85.0)
                            L_0x0202:
                                r24 = 0
                                boolean r25 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r25 == 0) goto L_0x020b
                                r25 = 1118437376(0x42aa0000, float:85.0)
                                goto L_0x020d
                            L_0x020b:
                                r25 = 0
                            L_0x020d:
                                r26 = 0
                                android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
                                r5.addView(r8, r7)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.firstNameField
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$W7zySTKCIVb3sSS-uWUkx-fDcA4 r8 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$W7zySTKCIVb3sSS-uWUkx-fDcA4
                                r8.<init>()
                                r7.setOnEditorActionListener(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                                r7.<init>(r2)
                                r0.lastNameField = r7
                                r8 = 2131691783(0x7f0f0907, float:1.9012648E38)
                                java.lang.String r13 = "LastName"
                                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
                                r7.setHint(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                                r7.setHintTextColor(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                                r7.setTextColor(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                android.graphics.drawable.Drawable r8 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r2, r3)
                                r7.setBackgroundDrawable(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                                r7.setCursorColor(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                                r7.setCursorSize(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r7.setCursorWidth(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r8 = 268435462(0x10000006, float:2.5243567E-29)
                                r7.setImeOptions(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r7.setTextSize(r4, r12)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r7.setMaxLines(r4)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r8 = 8192(0x2000, float:1.14794E-41)
                                r7.setInputType(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r20 = -1082130432(0xffffffffbf800000, float:-1.0)
                                r21 = 1108344832(0x42100000, float:36.0)
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x028a
                                r8 = 5
                                goto L_0x028b
                            L_0x028a:
                                r8 = 3
                            L_0x028b:
                                r22 = r8 | 48
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x0294
                                r23 = 0
                                goto L_0x0296
                            L_0x0294:
                                r23 = 1118437376(0x42aa0000, float:85.0)
                            L_0x0296:
                                r24 = 1112276992(0x424c0000, float:51.0)
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x029f
                                r25 = 1118437376(0x42aa0000, float:85.0)
                                goto L_0x02a1
                            L_0x029f:
                                r25 = 0
                            L_0x02a1:
                                r26 = 0
                                android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
                                r5.addView(r7, r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$QhBiWzhoGm1w0TQQy1Qd_I18KMM r8 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$QhBiWzhoGm1w0TQQy1Qd_I18KMM
                                r8.<init>()
                                r7.setOnEditorActionListener(r8)
                                android.widget.TextView r7 = new android.widget.TextView
                                r7.<init>(r2)
                                r0.wrongNumber = r7
                                r8 = 2131690322(0x7f0f0352, float:1.9009684E38)
                                java.lang.String r9 = "CancelRegistration"
                                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                                r7.setText(r8)
                                android.widget.TextView r7 = r0.wrongNumber
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x02cf
                                r8 = 5
                                goto L_0x02d0
                            L_0x02cf:
                                r8 = 3
                            L_0x02d0:
                                r8 = r8 | r4
                                r7.setGravity(r8)
                                android.widget.TextView r7 = r0.wrongNumber
                                java.lang.String r8 = "windowBackgroundWhiteBlueText4"
                                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                                r7.setTextColor(r8)
                                android.widget.TextView r7 = r0.wrongNumber
                                r8 = 1096810496(0x41600000, float:14.0)
                                r7.setTextSize(r4, r8)
                                android.widget.TextView r7 = r0.wrongNumber
                                r8 = 1073741824(0x40000000, float:2.0)
                                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                                float r8 = (float) r9
                                r9 = 1065353216(0x3f800000, float:1.0)
                                r7.setLineSpacing(r8, r9)
                                android.widget.TextView r7 = r0.wrongNumber
                                r8 = 1103101952(0x41c00000, float:24.0)
                                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                                r7.setPadding(r3, r8, r3, r3)
                                android.widget.TextView r3 = r0.wrongNumber
                                r7 = 8
                                r3.setVisibility(r7)
                                android.widget.TextView r3 = r0.wrongNumber
                                r20 = -2
                                r21 = -2
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r7 == 0) goto L_0x0312
                                r8 = 5
                                goto L_0x0313
                            L_0x0312:
                                r8 = 3
                            L_0x0313:
                                r22 = r8 | 48
                                r23 = 0
                                r24 = 20
                                r25 = 0
                                r26 = 0
                                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                                r0.addView(r3, r7)
                                android.widget.TextView r3 = r0.wrongNumber
                                im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$j2BH4JQJ2XX5ByRt_6S53a91Th0 r7 = new im.bclpbkiauv.ui.hui.login.-$$Lambda$HloginActivity$LoginActivityRegisterView$j2BH4JQJ2XX5ByRt_6S53a91Th0
                                r7.<init>()
                                r3.setOnClickListener(r7)
                                android.widget.TextView r3 = new android.widget.TextView
                                r3.<init>(r2)
                                r0.privacyView = r3
                                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                                r3.setTextColor(r6)
                                android.widget.TextView r3 = r0.privacyView
                                im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy r6 = new im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy
                                r6.<init>()
                                r3.setMovementMethod(r6)
                                android.widget.TextView r3 = r0.privacyView
                                java.lang.String r6 = "windowBackgroundWhiteLinkText"
                                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                                r3.setLinkTextColor(r6)
                                android.widget.TextView r3 = r0.privacyView
                                r6 = 1096810496(0x41600000, float:14.0)
                                r3.setTextSize(r4, r6)
                                android.widget.TextView r3 = r0.privacyView
                                r4 = 81
                                r3.setGravity(r4)
                                android.widget.TextView r3 = r0.privacyView
                                r4 = 1073741824(0x40000000, float:2.0)
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                                float r4 = (float) r4
                                r3.setLineSpacing(r4, r9)
                                android.widget.TextView r3 = r0.privacyView
                                r11 = -2
                                r12 = -1
                                r13 = 81
                                r14 = 0
                                r15 = 28
                                r16 = 0
                                r17 = 16
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                                r0.addView(r3, r4)
                                r3 = 2131694157(0x7f0f124d, float:1.9017463E38)
                                java.lang.String r4 = "TermsOfServiceLogin"
                                java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
                                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
                                r4.<init>(r3)
                                r6 = 42
                                int r7 = r3.indexOf(r6)
                                int r6 = r3.lastIndexOf(r6)
                                r8 = -1
                                if (r7 == r8) goto L_0x03b6
                                if (r6 == r8) goto L_0x03b6
                                if (r7 == r6) goto L_0x03b6
                                int r8 = r6 + 1
                                java.lang.String r9 = ""
                                r4.replace(r6, r8, r9)
                                int r8 = r7 + 1
                                r4.replace(r7, r8, r9)
                                im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$LinkSpan r8 = new im.bclpbkiauv.ui.hui.login.HloginActivity$LoginActivityRegisterView$LinkSpan
                                r8.<init>()
                                int r9 = r6 + -1
                                r11 = 33
                                r4.setSpan(r8, r7, r9, r11)
                            L_0x03b6:
                                android.widget.TextView r8 = r0.privacyView
                                r8.setText(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.login.HloginActivity.LoginActivityRegisterView.<init>(im.bclpbkiauv.ui.hui.login.HloginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ void lambda$new$5$HloginActivity$LoginActivityRegisterView(View view) {
                            this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
                                public final void run() {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$null$4$HloginActivity$LoginActivityRegisterView();
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$4$HloginActivity$LoginActivityRegisterView() {
                            this.avatar = null;
                            this.avatarBig = null;
                            this.uploadedAvatar = null;
                            showAvatarProgress(false, true);
                            this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
                            this.avatarEditor.setImageResource(R.drawable.actions_setphoto);
                        }

                        public /* synthetic */ boolean lambda$new$6$HloginActivity$LoginActivityRegisterView(TextView textView2, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            this.lastNameField.requestFocus();
                            return true;
                        }

                        public /* synthetic */ boolean lambda$new$7$HloginActivity$LoginActivityRegisterView(TextView textView2, int i, KeyEvent keyEvent) {
                            if (i != 6 && i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$8$HloginActivity$LoginActivityRegisterView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null) {
                                onBackPressed(false);
                            }
                        }

                        public void didUploadPhoto(TLRPC.InputFile file, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
                            AndroidUtilities.runOnUIThread(new Runnable(smallSize, bigSize) {
                                private final /* synthetic */ TLRPC.PhotoSize f$1;
                                private final /* synthetic */ TLRPC.PhotoSize f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$didUploadPhoto$9$HloginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$didUploadPhoto$9$HloginActivity$LoginActivityRegisterView(TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
                            this.avatar = smallSize.location;
                            this.avatarBig = bigSize.location;
                            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
                        }

                        private void showAvatarProgress(final boolean show, boolean animated) {
                            if (this.avatarEditor != null) {
                                AnimatorSet animatorSet = this.avatarAnimation;
                                if (animatorSet != null) {
                                    animatorSet.cancel();
                                    this.avatarAnimation = null;
                                }
                                if (animated) {
                                    this.avatarAnimation = new AnimatorSet();
                                    if (show) {
                                        this.avatarProgressView.setVisibility(0);
                                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                                    } else {
                                        this.avatarEditor.setVisibility(0);
                                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                                    }
                                    this.avatarAnimation.setDuration(180);
                                    this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            if (LoginActivityRegisterView.this.avatarAnimation != null && LoginActivityRegisterView.this.avatarEditor != null) {
                                                if (show) {
                                                    LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                                                } else {
                                                    LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                                                }
                                                AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
                                            }
                                        }

                                        public void onAnimationCancel(Animator animation) {
                                            AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
                                        }
                                    });
                                    this.avatarAnimation.start();
                                } else if (show) {
                                    this.avatarEditor.setAlpha(1.0f);
                                    this.avatarEditor.setVisibility(4);
                                    this.avatarProgressView.setAlpha(1.0f);
                                    this.avatarProgressView.setVisibility(0);
                                } else {
                                    this.avatarEditor.setAlpha(1.0f);
                                    this.avatarEditor.setVisibility(0);
                                    this.avatarProgressView.setAlpha(0.0f);
                                    this.avatarProgressView.setVisibility(4);
                                }
                            }
                        }

                        public boolean onBackPressed(boolean force) {
                            if (!force) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
                                builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        HloginActivity.LoginActivityRegisterView.this.lambda$onBackPressed$10$HloginActivity$LoginActivityRegisterView(dialogInterface, i);
                                    }
                                });
                                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                                return false;
                            }
                            this.this$0.needHideProgress(true);
                            this.nextPressed = false;
                            this.currentParams = null;
                            return true;
                        }

                        public /* synthetic */ void lambda$onBackPressed$10$HloginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }

                        public String getHeaderName() {
                            return LocaleController.getString("YourName", R.string.YourName);
                        }

                        public void onCancelPressed() {
                            this.nextPressed = false;
                        }

                        public boolean needBackButton() {
                            return true;
                        }

                        public void onShow() {
                            super.onShow();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$onShow$11$HloginActivity$LoginActivityRegisterView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$11$HloginActivity$LoginActivityRegisterView() {
                            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
                            if (editTextBoldCursor != null) {
                                editTextBoldCursor.requestFocus();
                                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                            }
                        }

                        public void setParams(Bundle params, boolean restore) {
                            if (params != null) {
                                this.firstNameField.setText("");
                                this.lastNameField.setText("");
                                this.requestPhone = params.getString("phoneFormated");
                                this.phoneHash = params.getString("phoneHash");
                                this.phoneCode = params.getString("code");
                                this.currentParams = params;
                            }
                        }

                        public void onNextPressed() {
                            if (!this.nextPressed) {
                                if (this.this$0.currentTermsOfService == null || !this.this$0.currentTermsOfService.popup) {
                                    this.nextPressed = true;
                                    TLRPC.TL_auth_signUp req = new TLRPC.TL_auth_signUp();
                                    req.phone_code_hash = this.phoneHash;
                                    req.phone_number = this.requestPhone;
                                    req.first_name = this.firstNameField.getText().toString();
                                    req.last_name = this.lastNameField.getText().toString();
                                    this.this$0.needShowProgress(0);
                                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            HloginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$13$HloginActivity$LoginActivityRegisterView(tLObject, tL_error);
                                        }
                                    }, 10);
                                    return;
                                }
                                showTermsOfService(true);
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$13$HloginActivity$LoginActivityRegisterView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error) {
                                private final /* synthetic */ TLRPC.TL_error f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    HloginActivity.LoginActivityRegisterView.this.lambda$null$12$HloginActivity$LoginActivityRegisterView(this.f$1);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$12$HloginActivity$LoginActivityRegisterView(TLRPC.TL_error error) {
                            this.nextPressed = false;
                            this.this$0.needHideProgress(false);
                            if (TextUtils.isEmpty(this.firstNameField.getText().toString().trim())) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("EmptyNameTips", R.string.EmptyNameTips));
                            } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                            } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                            } else if (error.text.contains("FIRSTNAME_INVALID")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidName", R.string.InvalidName));
                            } else if (error.text.contains("LASTNAME_INVALID")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidNickname", R.string.InvalidNickname));
                            } else if (error.text.contains("FIRSTNAME_LASTNAME_EMPTY")) {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("EmptyNameTips", R.string.EmptyNameTips));
                            } else {
                                this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }

                        public void saveStateParams(Bundle bundle) {
                            String first = this.firstNameField.getText().toString();
                            if (first.length() != 0) {
                                bundle.putString("registerview_first", first);
                            }
                            String last = this.lastNameField.getText().toString();
                            if (last.length() != 0) {
                                bundle.putString("registerview_last", last);
                            }
                            if (this.this$0.currentTermsOfService != null) {
                                SerializedData data = new SerializedData(this.this$0.currentTermsOfService.getObjectSize());
                                this.this$0.currentTermsOfService.serializeToStream(data);
                                bundle.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                                data.cleanup();
                            }
                            Bundle bundle2 = this.currentParams;
                            if (bundle2 != null) {
                                bundle.putBundle("registerview_params", bundle2);
                            }
                        }

                        public void restoreStateParams(Bundle bundle) {
                            byte[] arr;
                            Bundle bundle2 = bundle.getBundle("registerview_params");
                            this.currentParams = bundle2;
                            if (bundle2 != null) {
                                setParams(bundle2, true);
                            }
                            try {
                                String terms = bundle.getString("terms");
                                if (!(terms == null || (arr = Base64.decode(terms, 0)) == null)) {
                                    SerializedData data = new SerializedData(arr);
                                    TLRPC.TL_help_termsOfService unused = this.this$0.currentTermsOfService = TLRPC.TL_help_termsOfService.TLdeserialize(data, data.readInt32(false), false);
                                    data.cleanup();
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            String first = bundle.getString("registerview_first");
                            if (first != null) {
                                this.firstNameField.setText(first);
                            }
                            String last = bundle.getString("registerview_last");
                            if (last != null) {
                                this.lastNameField.setText(last);
                            }
                        }
                    }

                    public ThemeDescription[] getThemeDescriptions() {
                        int a = 0;
                        while (true) {
                            SlideView[] slideViewArr = this.views;
                            if (a >= slideViewArr.length) {
                                PhoneView phoneView = (PhoneView) slideViewArr[0];
                                LoginActivitySmsView smsView1 = (LoginActivitySmsView) slideViewArr[1];
                                LoginActivitySmsView smsView2 = (LoginActivitySmsView) slideViewArr[2];
                                LoginActivitySmsView smsView3 = (LoginActivitySmsView) slideViewArr[3];
                                LoginActivitySmsView smsView4 = (LoginActivitySmsView) slideViewArr[4];
                                LoginActivityRegisterView registerView = (LoginActivityRegisterView) slideViewArr[5];
                                LoginActivityPasswordView passwordView = (LoginActivityPasswordView) slideViewArr[6];
                                LoginActivityRecoverView recoverView = (LoginActivityRecoverView) slideViewArr[7];
                                LoginActivityResetWaitView waitView = (LoginActivityResetWaitView) slideViewArr[8];
                                ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                                ThemeDescription themeDescription = r12;
                                ThemeDescription themeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite);
                                arrayList.add(themeDescription);
                                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
                                arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
                                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
                                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
                                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
                                arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayLine));
                                arrayList.add(new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(passwordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(passwordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription(passwordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText6));
                                arrayList.add(new ThemeDescription(passwordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(registerView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(registerView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
                                arrayList.add(new ThemeDescription(recoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                arrayList.add(new ThemeDescription(recoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription(waitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(waitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(waitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText6));
                                arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView1.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                if (smsView1.codeField != null) {
                                    for (int a2 = 0; a2 < smsView1.codeField.length; a2++) {
                                        arrayList.add(new ThemeDescription(smsView1.codeField[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                        arrayList.add(new ThemeDescription(smsView1.codeField[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                    }
                                }
                                arrayList.add(new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressInner));
                                arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressOuter));
                                arrayList.add(new ThemeDescription(smsView1.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(smsView1.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground));
                                arrayList.add(new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                if (smsView2.codeField != null) {
                                    for (int a3 = 0; a3 < smsView2.codeField.length; a3++) {
                                        arrayList.add(new ThemeDescription(smsView2.codeField[a3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                        arrayList.add(new ThemeDescription(smsView2.codeField[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                    }
                                }
                                arrayList.add(new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressInner));
                                arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressOuter));
                                arrayList.add(new ThemeDescription(smsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(smsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground));
                                arrayList.add(new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                if (smsView3.codeField != null) {
                                    for (int a4 = 0; a4 < smsView3.codeField.length; a4++) {
                                        arrayList.add(new ThemeDescription(smsView3.codeField[a4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                        arrayList.add(new ThemeDescription(smsView3.codeField[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                    }
                                }
                                arrayList.add(new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressInner));
                                arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressOuter));
                                arrayList.add(new ThemeDescription(smsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(smsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground));
                                arrayList.add(new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                if (smsView4.codeField != null) {
                                    for (int a5 = 0; a5 < smsView4.codeField.length; a5++) {
                                        arrayList.add(new ThemeDescription(smsView4.codeField[a5], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                        arrayList.add(new ThemeDescription(smsView4.codeField[a5], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                                    }
                                }
                                arrayList.add(new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                                arrayList.add(new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                                arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressInner));
                                arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressOuter));
                                arrayList.add(new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                                arrayList.add(new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground));
                                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
                            } else if (slideViewArr[a] == null) {
                                return new ThemeDescription[0];
                            } else {
                                a++;
                            }
                        }
                    }
                }
