package im.bclpbkiauv.ui;

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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.blankj.utilcode.constant.TimeConstants;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.javaBean.fc.FollowedFcListBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
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
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.LoginActivity;
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
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import kotlin.text.Typography;
import org.slf4j.Marker;

@Deprecated
public class LoginActivity extends BaseFragment {
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

    public LoginActivity() {
    }

    public LoginActivity(int account) {
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
        AppPreferenceUtil.putString("PublishFcBean", "");
        FcDBHelper.getInstance().deleteAll(HomeFcListBean.class);
        FcDBHelper.getInstance().deleteAll(RecommendFcListBean.class);
        FcDBHelper.getInstance().deleteAll(FollowedFcListBean.class);
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    if (LoginActivity.this.doneProgressView.getTag() == null) {
                        LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
                    } else if (LoginActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) LoginActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
                        builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), (DialogInterface.OnClickListener) null);
                        builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                LoginActivity.AnonymousClass1.this.lambda$onItemClick$0$LoginActivity$1(dialogInterface, i);
                            }
                        });
                        LoginActivity.this.showDialog(builder.create());
                    }
                } else if (id == -1 && LoginActivity.this.onBackPressed()) {
                    LoginActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$LoginActivity$1(DialogInterface dialogInterface, int i) {
                LoginActivity.this.views[LoginActivity.this.currentViewNum].onCancelPressed();
                LoginActivity.this.needHideProgress(true);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.actionBar.setAllowOverlayTitle(true);
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
                if (LoginActivity.this.currentViewNum == 1 || LoginActivity.this.currentViewNum == 2 || LoginActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = LoginActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
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
            if (a2 < slideViewArr2.length) {
                if (savedInstanceState != null) {
                    if (a2 < 1 || a2 > 4) {
                        this.views[a2].restoreStateParams(savedInstanceState);
                    } else if (a2 == this.currentViewNum) {
                        slideViewArr2[a2].restoreStateParams(savedInstanceState);
                    }
                }
                if (this.currentViewNum == a2) {
                    this.actionBar.setBackButtonImage((this.views[a2].needBackButton() || this.newAccount) ? R.mipmap.ic_back : 0);
                    this.views[a2].setVisibility(0);
                    this.views[a2].onShow();
                    if (a2 == 3 || a2 == 8) {
                        this.doneItem.setVisibility(8);
                    }
                } else {
                    this.views[a2].setVisibility(8);
                }
                a2++;
            } else {
                this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
                return this.fragmentView;
            }
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
        if (ApplicationLoader.blnShowAuth && !UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            Intent intent = new Intent("app_user_authorize_result");
            intent.putExtra("login", "0");
            getParentActivity().sendBroadcast(intent);
        }
        ApplicationLoader.blnShowAuth = false;
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
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
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
                    LoginActivity.this.lambda$needShowInvalidAlert$0$LoginActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$needShowInvalidAlert$0$LoginActivity(boolean banned, String phoneNumber, DialogInterface dialog, int which) {
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
                if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                    if (!show) {
                        LoginActivity.this.doneProgressView.setVisibility(4);
                    } else {
                        LoginActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                    AnimatorSet unused = LoginActivity.this.doneItemAnimation = null;
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
            return;
        }
        int i2 = R.mipmap.ic_back;
        if (animated) {
            SlideView[] slideViewArr = this.views;
            final SlideView outView = slideViewArr[i];
            SlideView newView = slideViewArr[page];
            this.currentViewNum = page;
            ActionBar actionBar = this.actionBar;
            if (!newView.needBackButton() && !this.newAccount) {
                i2 = 0;
            }
            actionBar.setBackButtonImage(i2);
            newView.setParams(params, false);
            this.actionBar.setTitle(newView.getHeaderName());
            setParentActivityTitle(newView.getHeaderName());
            newView.onShow();
            int i3 = AndroidUtilities.displaySize.x;
            if (back) {
                i3 = -i3;
            }
            newView.setX((float) i3);
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
            int i4 = AndroidUtilities.displaySize.x;
            if (!back) {
                i4 = -i4;
            }
            fArr[0] = (float) i4;
            animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!this.views[page].needBackButton() && !this.newAccount) {
            i2 = 0;
        }
        actionBar2.setBackButtonImage(i2);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params, false);
        this.views[page].setVisibility(0);
        this.actionBar.setTitle(this.views[page].getHeaderName());
        setParentActivityTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
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
        AndroidUtilities.runOnUIThread($$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA.INSTANCE);
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
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public View view;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(im.bclpbkiauv.ui.LoginActivity r23, android.content.Context r24) {
            /*
                r22 = this;
                r1 = r22
                r2 = r23
                r3 = r24
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
                r1.countryButton = r5
                r6 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r1.countryButton
                r7 = 1094713344(0x41400000, float:12.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r9 = 1092616192(0x41200000, float:10.0)
                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r5.setPadding(r8, r10, r11, r0)
                android.widget.TextView r5 = r1.countryButton
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r5.setTextColor(r10)
                android.widget.TextView r5 = r1.countryButton
                r5.setMaxLines(r4)
                android.widget.TextView r5 = r1.countryButton
                r5.setSingleLine(r4)
                android.widget.TextView r5 = r1.countryButton
                android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r10)
                android.widget.TextView r5 = r1.countryButton
                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r11 = 5
                r12 = 3
                if (r10 == 0) goto L_0x007e
                r10 = 5
                goto L_0x007f
            L_0x007e:
                r10 = 3
            L_0x007f:
                r10 = r10 | r4
                r5.setGravity(r10)
                android.widget.TextView r5 = r1.countryButton
                r10 = 2131231608(0x7f080378, float:1.8079302E38)
                r5.setBackgroundResource(r10)
                android.widget.TextView r5 = r1.countryButton
                r13 = -1
                r14 = 36
                r15 = 0
                r16 = 0
                r17 = 0
                r18 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (float) r15, (float) r16, (float) r17, (float) r18)
                r1.addView(r5, r10)
                android.widget.TextView r5 = r1.countryButton
                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$m7fYx9bdSKMroD3QOtRe7YXy0LI r10 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$m7fYx9bdSKMroD3QOtRe7YXy0LI
                r10.<init>()
                r5.setOnClickListener(r10)
                android.view.View r5 = new android.view.View
                r5.<init>(r3)
                r1.view = r5
                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r5.setPadding(r10, r0, r7, r0)
                android.view.View r5 = r1.view
                java.lang.String r7 = "windowBackgroundWhiteGrayLine"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r5.setBackgroundColor(r7)
                android.view.View r5 = r1.view
                r14 = 1
                r15 = 1082130432(0x40800000, float:4.0)
                r16 = -1047789568(0xffffffffc18c0000, float:-17.5)
                r17 = 1082130432(0x40800000, float:4.0)
                r18 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (float) r15, (float) r16, (float) r17, (float) r18)
                r1.addView(r5, r7)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r3)
                r5.setOrientation(r0)
                r14 = -2
                r15 = 0
                r16 = 1101004800(0x41a00000, float:20.0)
                r17 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (float) r15, (float) r16, (float) r17, (float) r18)
                r1.addView(r5, r7)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r3)
                r1.textView = r7
                java.lang.String r10 = "+"
                r7.setText(r10)
                android.widget.TextView r7 = r1.textView
                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r7.setTextColor(r10)
                android.widget.TextView r7 = r1.textView
                r7.setTextSize(r4, r6)
                android.widget.TextView r7 = r1.textView
                r10 = -2
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r10)
                r5.addView(r7, r10)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                r7.<init>(r3)
                r1.codeField = r7
                r7.setInputType(r12)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r7.setTextColor(r10)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                android.graphics.drawable.Drawable r10 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r3, r0)
                r7.setBackgroundDrawable(r10)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r7.setCursorColor(r10)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r10 = 1101004800(0x41a00000, float:20.0)
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                r7.setCursorSize(r13)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r13 = 1069547520(0x3fc00000, float:1.5)
                r7.setCursorWidth(r13)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r7.setPadding(r9, r0, r0, r0)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r7.setTextSize(r4, r6)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r7.setMaxLines(r4)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r9 = 19
                r7.setGravity(r9)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r1.codeField
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r7.setImeOptions(r14)
                android.text.InputFilter[] r7 = new android.text.InputFilter[r4]
                android.text.InputFilter$LengthFilter r15 = new android.text.InputFilter$LengthFilter
                r15.<init>(r11)
                r7[r0] = r15
                im.bclpbkiauv.ui.components.EditTextBoldCursor r15 = r1.codeField
                r15.setFilters(r7)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r15 = r1.codeField
                r16 = 55
                r17 = 36
                r18 = -1055916032(0xffffffffc1100000, float:-9.0)
                r19 = 0
                r20 = 1098907648(0x41800000, float:16.0)
                r21 = 0
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r16, (int) r17, (float) r18, (float) r19, (float) r20, (float) r21)
                r5.addView(r15, r11)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r1.codeField
                im.bclpbkiauv.ui.LoginActivity$PhoneView$1 r15 = new im.bclpbkiauv.ui.LoginActivity$PhoneView$1
                r15.<init>(r2)
                r11.addTextChangedListener(r15)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r1.codeField
                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$lHdF8wv5FHf_MLE7gsdVwuGmy2w r15 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$lHdF8wv5FHf_MLE7gsdVwuGmy2w
                r15.<init>()
                r11.setOnEditorActionListener(r15)
                im.bclpbkiauv.ui.LoginActivity$PhoneView$2 r11 = new im.bclpbkiauv.ui.LoginActivity$PhoneView$2
                r11.<init>(r3, r2)
                r1.phoneField = r11
                r11.setInputType(r12)
                im.bclpbkiauv.ui.components.HintEditText r11 = r1.phoneField
                int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r11.setTextColor(r15)
                im.bclpbkiauv.ui.components.HintEditText r11 = r1.phoneField
                java.lang.String r15 = "windowBackgroundWhiteHintText"
                int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                r11.setHintTextColor(r15)
                im.bclpbkiauv.ui.components.HintEditText r11 = r1.phoneField
                android.graphics.drawable.Drawable r15 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r3, r0)
                r11.setBackgroundDrawable(r15)
                im.bclpbkiauv.ui.components.HintEditText r11 = r1.phoneField
                r11.setPadding(r0, r0, r0, r0)
                im.bclpbkiauv.ui.components.HintEditText r11 = r1.phoneField
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r11.setCursorColor(r8)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                r8.setCursorSize(r10)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                r8.setCursorWidth(r13)
                im.bclpbkiauv.ui.components.HintEditText r8 = r1.phoneField
                r8.setTextSize(r4, r6)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                r6.setMaxLines(r4)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                r6.setGravity(r9)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                r6.setImeOptions(r14)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                r8 = -1
                r9 = 1108344832(0x42100000, float:36.0)
                android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9)
                r5.addView(r6, r8)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                im.bclpbkiauv.ui.LoginActivity$PhoneView$3 r8 = new im.bclpbkiauv.ui.LoginActivity$PhoneView$3
                r8.<init>(r2)
                r6.addTextChangedListener(r8)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$3kUNOjuJ7xCMKZe1rZzAHpYbmRs r8 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$3kUNOjuJ7xCMKZe1rZzAHpYbmRs
                r8.<init>()
                r6.setOnEditorActionListener(r8)
                im.bclpbkiauv.ui.components.HintEditText r6 = r1.phoneField
                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$V-hbC2Z_1ZbnAJAvcpkXxGbafZA r8 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$PhoneView$V-hbC2Z_1ZbnAJAvcpkXxGbafZA
                r8.<init>()
                r6.setOnKeyListener(r8)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r3)
                r1.textView2 = r6
                r8 = 2131694034(0x7f0f11d2, float:1.9017213E38)
                java.lang.String r9 = "StartText"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r6.setText(r8)
                android.widget.TextView r6 = r1.textView2
                java.lang.String r8 = "windowBackgroundWhiteGrayText6"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r6.setTextColor(r8)
                android.widget.TextView r6 = r1.textView2
                r8 = 1096810496(0x41600000, float:14.0)
                r6.setTextSize(r4, r8)
                android.widget.TextView r6 = r1.textView2
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x0251
                r8 = 5
                goto L_0x0252
            L_0x0251:
                r8 = 3
            L_0x0252:
                r6.setGravity(r8)
                android.widget.TextView r6 = r1.textView2
                r8 = 1073741824(0x40000000, float:2.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                r9 = 1065353216(0x3f800000, float:1.0)
                r6.setLineSpacing(r8, r9)
                android.widget.TextView r6 = r1.textView2
                r13 = -2
                r14 = -2
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x026d
                r15 = 5
                goto L_0x026e
            L_0x026d:
                r15 = 3
            L_0x026e:
                r16 = 0
                r17 = 28
                r18 = 0
                r19 = 10
                android.widget.LinearLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r1.addView(r6, r8)
                boolean r6 = r23.newAccount
                r8 = 2
                if (r6 == 0) goto L_0x02bc
                im.bclpbkiauv.ui.cells.CheckBoxCell r6 = new im.bclpbkiauv.ui.cells.CheckBoxCell
                r6.<init>(r3, r8)
                r1.checkBoxCell = r6
                r9 = 2131694107(0x7f0f121b, float:1.9017361E38)
                java.lang.String r10 = "SyncContacts"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
                boolean r10 = r23.syncContacts
                java.lang.String r11 = ""
                r6.setText(r9, r11, r10, r0)
                im.bclpbkiauv.ui.cells.CheckBoxCell r6 = r1.checkBoxCell
                r13 = -2
                r14 = -1
                r15 = 51
                r16 = 0
                r17 = 0
                r18 = 0
                r19 = 0
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r1.addView(r6, r9)
                im.bclpbkiauv.ui.cells.CheckBoxCell r6 = r1.checkBoxCell
                im.bclpbkiauv.ui.LoginActivity$PhoneView$4 r9 = new im.bclpbkiauv.ui.LoginActivity$PhoneView$4
                r9.<init>(r2)
                r6.setOnClickListener(r9)
            L_0x02bc:
                java.util.HashMap r6 = new java.util.HashMap
                r6.<init>()
                java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0318 }
                java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0318 }
                android.content.res.Resources r11 = r22.getResources()     // Catch:{ Exception -> 0x0318 }
                android.content.res.AssetManager r11 = r11.getAssets()     // Catch:{ Exception -> 0x0318 }
                java.lang.String r13 = "countries.txt"
                java.io.InputStream r11 = r11.open(r13)     // Catch:{ Exception -> 0x0318 }
                r10.<init>(r11)     // Catch:{ Exception -> 0x0318 }
                r9.<init>(r10)     // Catch:{ Exception -> 0x0318 }
            L_0x02d9:
                java.lang.String r10 = r9.readLine()     // Catch:{ Exception -> 0x0318 }
                r11 = r10
                if (r10 == 0) goto L_0x0314
                java.lang.String r10 = ";"
                java.lang.String[] r10 = r11.split(r10)     // Catch:{ Exception -> 0x0318 }
                java.util.ArrayList<java.lang.String> r13 = r1.countriesArray     // Catch:{ Exception -> 0x0318 }
                r14 = r10[r8]     // Catch:{ Exception -> 0x0318 }
                r13.add(r0, r14)     // Catch:{ Exception -> 0x0318 }
                java.util.HashMap<java.lang.String, java.lang.String> r13 = r1.countriesMap     // Catch:{ Exception -> 0x0318 }
                r14 = r10[r8]     // Catch:{ Exception -> 0x0318 }
                r15 = r10[r0]     // Catch:{ Exception -> 0x0318 }
                r13.put(r14, r15)     // Catch:{ Exception -> 0x0318 }
                java.util.HashMap<java.lang.String, java.lang.String> r13 = r1.codesMap     // Catch:{ Exception -> 0x0318 }
                r14 = r10[r0]     // Catch:{ Exception -> 0x0318 }
                r15 = r10[r8]     // Catch:{ Exception -> 0x0318 }
                r13.put(r14, r15)     // Catch:{ Exception -> 0x0318 }
                int r13 = r10.length     // Catch:{ Exception -> 0x0318 }
                if (r13 <= r12) goto L_0x030b
                java.util.HashMap<java.lang.String, java.lang.String> r13 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0318 }
                r14 = r10[r0]     // Catch:{ Exception -> 0x0318 }
                r15 = r10[r12]     // Catch:{ Exception -> 0x0318 }
                r13.put(r14, r15)     // Catch:{ Exception -> 0x0318 }
            L_0x030b:
                r13 = r10[r4]     // Catch:{ Exception -> 0x0318 }
                r14 = r10[r8]     // Catch:{ Exception -> 0x0318 }
                r6.put(r13, r14)     // Catch:{ Exception -> 0x0318 }
                goto L_0x02d9
            L_0x0314:
                r9.close()     // Catch:{ Exception -> 0x0318 }
                goto L_0x031c
            L_0x0318:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x031c:
                java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
                im.bclpbkiauv.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r8 = im.bclpbkiauv.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
                java.util.Collections.sort(r0, r8)
                r8 = 0
                android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0332 }
                java.lang.String r9 = "phone"
                java.lang.Object r0 = r0.getSystemService(r9)     // Catch:{ Exception -> 0x0332 }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0332 }
                if (r0 == 0) goto L_0x0331
                r8 = 0
            L_0x0331:
                goto L_0x0336
            L_0x0332:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0336:
                if (r8 == 0) goto L_0x033f
                java.lang.String r0 = r8.toUpperCase()
                r1.setCountry(r6, r0)
            L_0x033f:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x035d
                android.widget.TextView r0 = r1.countryButton
                r9 = 2131690589(0x7f0f045d, float:1.9010226E38)
                java.lang.String r10 = "ChooseCountry"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
                r0.setText(r9)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r9 = 0
                r0.setHintText(r9)
                r1.countryState = r4
            L_0x035d:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x0374
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.requestFocus()
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r4 = r0.length()
                r0.setSelection(r4)
                goto L_0x0379
            L_0x0374:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.requestFocus()
            L_0x0379:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.PhoneView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context):void");
        }

        public /* synthetic */ void lambda$new$2$LoginActivity$PhoneView(View view2) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    LoginActivity.PhoneView.this.lambda$null$1$LoginActivity$PhoneView(country);
                }
            });
            this.this$0.presentFragment(fragment);
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry((String) null, country);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$null$0$LoginActivity$PhoneView();
                }
            }, 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        public /* synthetic */ void lambda$null$0$LoginActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        public /* synthetic */ boolean lambda$new$3$LoginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        public /* synthetic */ boolean lambda$new$4$LoginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ boolean lambda$new$5$LoginActivity$PhoneView(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
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
                                        LoginActivity.PhoneView.this.lambda$onNextPressed$6$LoginActivity$PhoneView(this.f$1, dialogInterface, i);
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
                            LoginActivity.PhoneView.this.lambda$onNextPressed$8$LoginActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 27));
                }
            }
        }

        public /* synthetic */ void lambda$onNextPressed$6$LoginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(num, false);
            }
            this.this$0.finishFragment();
        }

        public /* synthetic */ void lambda$onNextPressed$8$LoginActivity$PhoneView(Bundle params, TLRPC.TL_auth_sendCode req, TLObject response, TLRPC.TL_error error) {
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
                    LoginActivity.PhoneView.this.lambda$null$7$LoginActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$7$LoginActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_sendCode req) {
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
                    LoginActivity.PhoneView.this.lambda$onShow$9$LoginActivity$PhoneView();
                }
            }, 100);
        }

        public /* synthetic */ void lambda$onShow$9$LoginActivity$PhoneView() {
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
        public AppCompatTextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        /* access modifiers changed from: private */
        public String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public int time = TimeConstants.MIN;
        /* access modifiers changed from: private */
        public AppCompatTextView timeText;
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
        public LoginActivitySmsView(im.bclpbkiauv.ui.LoginActivity r31, android.content.Context r32, int r33) {
            /*
                r30 = this;
                r0 = r30
                r1 = r31
                r2 = r32
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
                r3 = r33
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
                if (r11 == 0) goto L_0x007d
                r11 = 5
                goto L_0x007e
            L_0x007d:
                r11 = 3
            L_0x007e:
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
                if (r5 != r13) goto L_0x012c
                android.widget.TextView r5 = r0.confirmTextView
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x009f
                r9 = 5
                goto L_0x00a0
            L_0x009f:
                r9 = 3
            L_0x00a0:
                r9 = r9 | 48
                r5.setGravity(r9)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00b0
                r9 = 5
                goto L_0x00b1
            L_0x00b0:
                r9 = 3
            L_0x00b1:
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r5, r9)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r15 = 2131231426(0x7f0802c2, float:1.8078933E38)
                r9.setImageResource(r15)
                boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x00fb
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
                if (r18 == 0) goto L_0x00e9
                r18 = 5
                goto L_0x00eb
            L_0x00e9:
                r18 = 3
            L_0x00eb:
                r19 = 1118044160(0x42a40000, float:82.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r15, r12)
                goto L_0x012a
            L_0x00fb:
                android.widget.TextView r12 = r0.confirmTextView
                r15 = -1082130432(0xffffffffbf800000, float:-1.0)
                r16 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r17 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x0108
                r17 = 5
                goto L_0x010a
            L_0x0108:
                r17 = 3
            L_0x010a:
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
            L_0x012a:
                goto L_0x0210
            L_0x012c:
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r11)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
                int r12 = r0.currentType
                java.lang.String r15 = "chats_actionBackground"
                if (r12 != r4) goto L_0x01ac
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.blackImageView = r12
                r11 = 2131231607(0x7f080377, float:1.80793E38)
                r12.setImageResource(r11)
                android.widget.ImageView r11 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
                r12.<init>(r9, r7)
                r11.setColorFilter(r12)
                android.widget.ImageView r7 = r0.blackImageView
                r23 = -1073741824(0xffffffffc0000000, float:-2.0)
                r24 = -1073741824(0xffffffffc0000000, float:-2.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131231605(0x7f080375, float:1.8079296E38)
                r7.setImageResource(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r11, r12)
                r7.setColorFilter(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131693850(0x7f0f111a, float:1.901684E38)
                java.lang.String r11 = "SentAppCodeTitle"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
                goto L_0x01ee
            L_0x01ac:
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131231606(0x7f080376, float:1.8079298E38)
                r7.setImageResource(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r11, r12)
                r7.setColorFilter(r9)
                android.widget.ImageView r7 = r0.blueImageView
                r23 = -1073741824(0xffffffffc0000000, float:-2.0)
                r24 = -1073741824(0xffffffffc0000000, float:-2.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131693854(0x7f0f111e, float:1.9016848E38)
                java.lang.String r11 = "SentSmsCodeTitle"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
            L_0x01ee:
                android.widget.TextView r7 = r0.titleTextView
                r23 = -2
                r24 = -2
                r25 = 49
                r26 = 0
                r27 = 18
                r28 = 0
                r29 = 0
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
                android.widget.TextView r7 = r0.confirmTextView
                r27 = 17
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
            L_0x0210:
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r2)
                r0.codeFieldContainer = r5
                r7 = 0
                r5.setOrientation(r7)
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 36
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r9, (int) r4)
                r0.addView(r5, r9)
                int r5 = r0.currentType
                if (r5 != r13) goto L_0x0231
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 8
                r5.setVisibility(r9)
            L_0x0231:
                im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$1 r5 = new im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$1
                r5.<init>(r2, r1)
                r0.timeText = r5
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r6)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r10)
                int r5 = r0.currentType
                r6 = 1097859072(0x41700000, float:15.0)
                r9 = 1092616192(0x41200000, float:10.0)
                if (r5 != r13) goto L_0x0291
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r11)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0260
                r11 = 5
                goto L_0x0261
            L_0x0260:
                r11 = 3
            L_0x0261:
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r11)
                im.bclpbkiauv.ui.LoginActivity$ProgressView r5 = new im.bclpbkiauv.ui.LoginActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0277
                r12 = 5
                goto L_0x0278
            L_0x0277:
                r12 = 3
            L_0x0278:
                r5.setGravity(r12)
                im.bclpbkiauv.ui.LoginActivity$ProgressView r5 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
                r0.addView(r5, r11)
                goto L_0x02b3
            L_0x0291:
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r11, r7, r12)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                r5.setTextSize(r4, r6)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                r11 = 49
                r5.setGravity(r11)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.timeText
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
            L_0x02b3:
                im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$2 r5 = new im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$2
                r5.<init>(r2, r1)
                r0.problemText = r5
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
                r5.setTextColor(r11)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.problemText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.problemText
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r8, r7, r9)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.problemText
                r5.setTextSize(r4, r6)
                androidx.appcompat.widget.AppCompatTextView r5 = r0.problemText
                r6 = 49
                r5.setGravity(r6)
                int r5 = r0.currentType
                if (r5 != r4) goto L_0x02fa
                androidx.appcompat.widget.AppCompatTextView r4 = r0.problemText
                r5 = 2131690916(0x7f0f05a4, float:1.901089E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x0308
            L_0x02fa:
                androidx.appcompat.widget.AppCompatTextView r4 = r0.problemText
                r5 = 2131690915(0x7f0f05a3, float:1.9010887E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x0308:
                androidx.appcompat.widget.AppCompatTextView r4 = r0.problemText
                r5 = 49
                android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r5)
                r0.addView(r4, r5)
                androidx.appcompat.widget.AppCompatTextView r4 = r0.problemText
                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$kgNQbS8qZva4DP3-wbmLJwzyMxg r5 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$kgNQbS8qZva4DP3-wbmLJwzyMxg
                r5.<init>()
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context, int):void");
        }

        public /* synthetic */ void lambda$new$0$LoginActivity$LoginActivitySmsView(View v) {
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
                    AppCompatTextView appCompatTextView = this.problemText;
                    appCompatTextView.layout(appCompatTextView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    AppCompatTextView appCompatTextView2 = this.timeText;
                    appCompatTextView2.layout(appCompatTextView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
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
                    LoginActivity.LoginActivitySmsView.this.lambda$resendCode$2$LoginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                }
            }, 10);
            this.this$0.needShowProgress(0);
        }

        public /* synthetic */ void lambda$resendCode$2$LoginActivity$LoginActivitySmsView(Bundle params, TLObject response, TLRPC.TL_error error) {
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
                    LoginActivity.LoginActivitySmsView.this.lambda$null$1$LoginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView(TLRPC.TL_error error, Bundle params, TLObject response) {
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
                    LoginActivity loginActivity = this.this$0;
                    String string = LocaleController.getString("AppName", R.string.AppName);
                    loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
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
                int i6 = 8;
                int i7 = 0;
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
                        this.codeField[a2].setGravity(49);
                        if (this.currentType == 3) {
                            this.codeField[a2].setEnabled(false);
                            this.codeField[a2].setInputType(0);
                            this.codeField[a2].setVisibility(i6);
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
                                return LoginActivity.LoginActivitySmsView.this.lambda$setParams$3$LoginActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
                            }
                        });
                        this.codeField[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                return LoginActivity.LoginActivitySmsView.this.lambda$setParams$4$LoginActivity$LoginActivitySmsView(textView, i, keyEvent);
                            }
                        });
                        a2++;
                        i6 = 8;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    int i8 = this.currentType;
                    if (i8 == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", R.string.SentAppCode));
                    } else if (i8 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (i8 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (i8 == 4) {
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
                    int i9 = this.currentType;
                    if (i9 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                        return;
                    }
                    String str2 = null;
                    if (i9 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i10 = this.nextType;
                        if (i10 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                        } else if (i10 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                        }
                        if (this.isRestored) {
                            str2 = AndroidUtilities.obtainLoginPhoneCall(this.pattern);
                        }
                        String callLogNumber = str2;
                        if (callLogNumber != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(callLogNumber);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                            return;
                        }
                        String str3 = this.catchedPhone;
                        if (str3 != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(str3);
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
                        AppCompatTextView appCompatTextView = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        appCompatTextView.setVisibility(i7);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$3$LoginActivity$LoginActivitySmsView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        public /* synthetic */ boolean lambda$setParams$4$LoginActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                              (wrap: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8 : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8) = 
                              (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR)
                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.4.run():void, dex: classes2.dex
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
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8) = 
                              (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.4.run():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8 r0 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$4$PyVV0lMmfvAkd-jmbJ2NSH6v8g8
                            r0.<init>(r1)
                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.AnonymousClass4.run():void");
                    }

                    public /* synthetic */ void lambda$run$0$LoginActivity$LoginActivitySmsView$4() {
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
                                  (wrap: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U : 0x000b: CONSTRUCTOR  (r0v2 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U) = 
                                  (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                 call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.run():void, dex: classes2.dex
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
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: CONSTRUCTOR  (r0v2 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U) = 
                                  (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                 call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.run():void, dex: classes2.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 90 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 96 more
                                */
                            /*
                                this = this;
                                im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r0 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                java.util.Timer r0 = r0.timeTimer
                                if (r0 != 0) goto L_0x0009
                                return
                            L_0x0009:
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U r0 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$cBH8QBzJGPqMJQcYgb-vVaZpy-U
                                r0.<init>(r1)
                                im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.AnonymousClass5.run():void");
                        }

                        public /* synthetic */ void lambda$run$2$LoginActivity$LoginActivitySmsView$5() {
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
                                          (wrap: im.bclpbkiauv.ui.LoginActivity : 0x0189: IGET  (r5v10 im.bclpbkiauv.ui.LoginActivity) = 
                                          (wrap: im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView : 0x0187: IGET  (r5v9 im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                         im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.this$1 im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView)
                                         im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this$0 im.bclpbkiauv.ui.LoginActivity)
                                         im.bclpbkiauv.ui.LoginActivity.access$5300(im.bclpbkiauv.ui.LoginActivity):int type: STATIC)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                          (r4v16 'req' im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode)
                                          (wrap: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I : 0x0195: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                         call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR)
                                          (10 int)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate, int):int type: VIRTUAL in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.lambda$run$2$LoginActivity$LoginActivitySmsView$5():void, dex: classes2.dex
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
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0195: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                         call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.lambda$run$2$LoginActivity$LoginActivitySmsView$5():void, dex: classes2.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 99 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I, state: NOT_LOADED
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
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r2 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        double r2 = r2.lastCurrentTime
                                        double r2 = r0 - r2
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        double unused = r4.lastCurrentTime = r0
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r5 = r4.time
                                        double r5 = (double) r5
                                        double r5 = r5 - r2
                                        int r5 = (int) r5
                                        int unused = r4.time = r5
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.time
                                        r5 = 1065353216(0x3f800000, float:1.0)
                                        r6 = 3
                                        r7 = 1000(0x3e8, float:1.401E-42)
                                        r8 = 4
                                        r9 = 2
                                        r10 = 0
                                        if (r4 < r7) goto L_0x00bf
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.time
                                        int r4 = r4 / r7
                                        int r4 = r4 / 60
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r11 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r11 = r11.time
                                        int r11 = r11 / r7
                                        int r7 = r4 * 60
                                        int r11 = r11 - r7
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r7 = r7.nextType
                                        r12 = 1
                                        if (r7 == r8) goto L_0x007c
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r7 = r7.nextType
                                        if (r7 != r6) goto L_0x0053
                                        goto L_0x007c
                                    L_0x0053:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r6 = r6.nextType
                                        if (r6 != r9) goto L_0x009c
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        androidx.appcompat.widget.AppCompatTextView r6 = r6.timeText
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
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        androidx.appcompat.widget.AppCompatTextView r6 = r6.timeText
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
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.LoginActivity$ProgressView r6 = r6.progressView
                                        if (r6 == 0) goto L_0x00bd
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.LoginActivity$ProgressView r6 = r6.progressView
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r7 = r7.time
                                        float r7 = (float) r7
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r8 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r8 = r8.timeout
                                        float r8 = (float) r8
                                        float r7 = r7 / r8
                                        float r5 = r5 - r7
                                        r6.setProgress(r5)
                                    L_0x00bd:
                                        goto L_0x019e
                                    L_0x00bf:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.LoginActivity$ProgressView r4 = r4.progressView
                                        if (r4 == 0) goto L_0x00d0
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.LoginActivity$ProgressView r4 = r4.progressView
                                        r4.setProgress(r5)
                                    L_0x00d0:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.destroyTimer()
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 != r6) goto L_0x00fa
                                        im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForCall(r10)
                                        im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                        int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveCall
                                        r4.removeObserver(r13, r5)
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        boolean unused = r4.waitingForEvent = r10
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.destroyCodeTimer()
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.resendCode()
                                        goto L_0x019e
                                    L_0x00fa:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 == r9) goto L_0x010a
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.currentType
                                        if (r4 != r8) goto L_0x019e
                                    L_0x010a:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 == r8) goto L_0x0140
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r9) goto L_0x011b
                                        goto L_0x0140
                                    L_0x011b:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r6) goto L_0x013f
                                        im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForSms(r10)
                                        im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                        int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveSmsCode
                                        r4.removeObserver(r13, r5)
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        boolean unused = r4.waitingForEvent = r10
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.destroyCodeTimer()
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.resendCode()
                                        goto L_0x019e
                                    L_0x013f:
                                        goto L_0x019e
                                    L_0x0140:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        int r4 = r4.nextType
                                        if (r4 != r8) goto L_0x015b
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        androidx.appcompat.widget.AppCompatTextView r4 = r4.timeText
                                        r5 = 2131690302(0x7f0f033e, float:1.9009644E38)
                                        java.lang.String r6 = "Calling"
                                        java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                        r4.setText(r5)
                                        goto L_0x016d
                                    L_0x015b:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        androidx.appcompat.widget.AppCompatTextView r4 = r4.timeText
                                        r5 = 2131693842(0x7f0f1112, float:1.9016824E38)
                                        java.lang.String r6 = "SendingSms"
                                        java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                        r4.setText(r5)
                                    L_0x016d:
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        r4.createCodeTimer()
                                        im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode
                                        r4.<init>()
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        java.lang.String r5 = r5.requestPhone
                                        r4.phone_number = r5
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        java.lang.String r5 = r5.phoneHash
                                        r4.phone_code_hash = r5
                                        im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.this
                                        im.bclpbkiauv.ui.LoginActivity r5 = r5.this$0
                                        int r5 = r5.currentAccount
                                        im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
                                        im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I r6 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$VzR-5EzTPLzvdE6ypJW2Vga4_9I
                                        r6.<init>(r13)
                                        r7 = 10
                                        r5.sendRequest(r4, r6, r7)
                                        goto L_0x013f
                                    L_0x019e:
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.AnonymousClass5.lambda$run$2$LoginActivity$LoginActivitySmsView$5():void");
                                }

                                public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView$5(TLObject response, TLRPC.TL_error error) {
                                    if (error != null && error.text != null) {
                                        AndroidUtilities.runOnUIThread(
                                        /*  JADX ERROR: Method code generation error
                                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                              (wrap: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk : 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk) = 
                                              (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                              (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                             call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR)
                                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.lambda$null$1$LoginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk) = 
                                              (r1v0 'this' im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5 A[THIS])
                                              (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                             call: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk.<init>(im.bclpbkiauv.ui.LoginActivity$LoginActivitySmsView$5, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.5.lambda$null$1$LoginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	... 90 more
                                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk, state: NOT_LOADED
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
                                            im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk r0 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$5$SzZJktBKv-mdzwVHOK-S9xNAsPk
                                            r0.<init>(r1, r3)
                                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                        L_0x000e:
                                            return
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivitySmsView.AnonymousClass5.lambda$null$1$LoginActivity$LoginActivitySmsView$5(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                                    }

                                    public /* synthetic */ void lambda$null$0$LoginActivity$LoginActivitySmsView$5(TLRPC.TL_error error) {
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
                                        LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$8$LoginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                                    }
                                }, 10));
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$8$LoginActivity$LoginActivitySmsView(TLRPC.TL_auth_signIn req, TLObject response, TLRPC.TL_error error) {
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
                                    LoginActivity.LoginActivitySmsView.this.lambda$null$7$LoginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$7$LoginActivity$LoginActivitySmsView(TLRPC.TL_error error, TLObject response, TLRPC.TL_auth_signIn req) {
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
                                            LoginActivity.LoginActivitySmsView.this.lambda$null$6$LoginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
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
                                    this.nextPressed = false;
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
                                            this.nextPressed = true;
                                            this.this$0.setPage(0, true, (Bundle) null, true);
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                            this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else {
                                            LoginActivity loginActivity = this.this$0;
                                            String string = LocaleController.getString("AppName", R.string.AppName);
                                            loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                        }
                                    }
                                }
                            }
                            if (ok && this.currentType == 3) {
                                AndroidUtilities.endIncomingCall();
                            }
                        }

                        public /* synthetic */ void lambda$null$6$LoginActivity$LoginActivitySmsView(TLRPC.TL_auth_signIn req, TLObject response1, TLRPC.TL_error error1) {
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
                                    LoginActivity.LoginActivitySmsView.this.lambda$null$5$LoginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$5$LoginActivity$LoginActivitySmsView(TLRPC.TL_error error1, TLObject response1, TLRPC.TL_auth_signIn req) {
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
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                                builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        LoginActivity.LoginActivitySmsView.this.lambda$onBackPressed$9$LoginActivity$LoginActivitySmsView(dialogInterface, i);
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
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, $$Lambda$LoginActivity$LoginActivitySmsView$fWOSXc0m1sQ4rn7pyhfapy_J_0.INSTANCE, 10);
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

                        public /* synthetic */ void lambda$onBackPressed$9$LoginActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }

                        static /* synthetic */ void lambda$onBackPressed$10(TLObject response, TLRPC.TL_error error) {
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
                                        LoginActivity.LoginActivitySmsView.this.lambda$onShow$11$LoginActivity$LoginActivitySmsView();
                                    }
                                }, 100);
                            }
                        }

                        public /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivitySmsView() {
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
                        final /* synthetic */ LoginActivity this$0;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityPasswordView(im.bclpbkiauv.ui.LoginActivity r21, android.content.Context r22) {
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
                                if (r7 == 0) goto L_0x0031
                                r7 = 5
                                goto L_0x0032
                            L_0x0031:
                                r7 = 3
                            L_0x0032:
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
                                if (r10 == 0) goto L_0x0059
                                r10 = 5
                                goto L_0x005a
                            L_0x0059:
                                r10 = 3
                            L_0x005a:
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
                                if (r13 == 0) goto L_0x00ea
                                r13 = 5
                                goto L_0x00eb
                            L_0x00ea:
                                r13 = 3
                            L_0x00eb:
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
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$xFRh1SVU0G8yh8DZbPoWumo8oJo r13 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$xFRh1SVU0G8yh8DZbPoWumo8oJo
                                r13.<init>()
                                r4.setOnEditorActionListener(r13)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.cancelButton = r4
                                boolean r13 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r13 == 0) goto L_0x011a
                                r13 = 5
                                goto L_0x011b
                            L_0x011a:
                                r13 = 3
                            L_0x011b:
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
                                if (r14 == 0) goto L_0x015b
                                r14 = 5
                                goto L_0x015c
                            L_0x015b:
                                r14 = 3
                            L_0x015c:
                                r14 = r14 | 48
                                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r12, (int) r14)
                                r0.addView(r4, r12)
                                android.widget.TextView r4 = r0.cancelButton
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$5TWzmLZxY0O-yDieZiDZfAKCQ4M r12 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$5TWzmLZxY0O-yDieZiDZfAKCQ4M
                                r12.<init>()
                                r4.setOnClickListener(r12)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountButton = r4
                                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r12 == 0) goto L_0x017c
                                r12 = 5
                                goto L_0x017d
                            L_0x017c:
                                r12 = 3
                            L_0x017d:
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
                                if (r10 == 0) goto L_0x01d0
                                r10 = 5
                                goto L_0x01d1
                            L_0x01d0:
                                r10 = 3
                            L_0x01d1:
                                r15 = r10 | 48
                                r16 = 0
                                r17 = 34
                                r18 = 0
                                r19 = 0
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                                r0.addView(r4, r10)
                                android.widget.TextView r4 = r0.resetAccountButton
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$BGunSRtUKufeLIJe7qOzyrwQehM r10 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityPasswordView$BGunSRtUKufeLIJe7qOzyrwQehM
                                r10.<init>()
                                r4.setOnClickListener(r10)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountText = r4
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x01f9
                                r10 = 5
                                goto L_0x01fa
                            L_0x01f9:
                                r10 = 3
                            L_0x01fa:
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
                                if (r4 == 0) goto L_0x0233
                                goto L_0x0234
                            L_0x0233:
                                r8 = 3
                            L_0x0234:
                                r12 = r8 | 48
                                r13 = 0
                                r14 = 7
                                r15 = 0
                                r16 = 14
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                                r0.addView(r3, r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivityPasswordView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityPasswordView(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$4$LoginActivity$LoginActivityPasswordView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null) {
                                if (this.has_recovery) {
                                    this.this$0.needShowProgress(0);
                                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            LoginActivity.LoginActivityPasswordView.this.lambda$null$3$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
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

                        public /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error, response) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$2$LoginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityPasswordView(TLRPC.TL_error error, TLObject response) {
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
                                        LoginActivity.LoginActivityPasswordView.this.lambda$null$1$LoginActivity$LoginActivityPasswordView(this.f$1, dialogInterface, i);
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

                        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityPasswordView(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
                            Bundle bundle = new Bundle();
                            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
                            this.this$0.setPage(7, true, bundle, false);
                        }

                        public /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityPasswordView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        LoginActivity.LoginActivityPasswordView.this.lambda$null$7$LoginActivity$LoginActivityPasswordView(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                            }
                        }

                        public /* synthetic */ void lambda$null$7$LoginActivity$LoginActivityPasswordView(DialogInterface dialogInterface, int i) {
                            this.this$0.needShowProgress(0);
                            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
                            req.reason = "Forgot password";
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$6$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                }
                            }, 10);
                        }

                        public /* synthetic */ void lambda$null$6$LoginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error) {
                                private final /* synthetic */ TLRPC.TL_error f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$5$LoginActivity$LoginActivityPasswordView(this.f$1);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$5$LoginActivity$LoginActivityPasswordView(TLRPC.TL_error error) {
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
                                        LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$13$LoginActivity$LoginActivityPasswordView(this.f$1);
                                    }
                                });
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$13$LoginActivity$LoginActivityPasswordView(String oldPassword) {
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
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$12$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
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

                        public /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityPasswordView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error, response) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$11$LoginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$11$LoginActivity$LoginActivityPasswordView(TLRPC.TL_error error, TLObject response) {
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
                                        LoginActivity.LoginActivityPasswordView.this.lambda$null$10$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
                                    }
                                }, 8);
                            }
                        }

                        public /* synthetic */ void lambda$null$10$LoginActivity$LoginActivityPasswordView(TLObject response2, TLRPC.TL_error error2) {
                            AndroidUtilities.runOnUIThread(new Runnable(error2, response2) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ TLObject f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityPasswordView.this.lambda$null$9$LoginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$9$LoginActivity$LoginActivityPasswordView(TLRPC.TL_error error2, TLObject response2) {
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
                                    LoginActivity.LoginActivityPasswordView.this.lambda$onShow$14$LoginActivity$LoginActivityPasswordView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$14$LoginActivity$LoginActivityPasswordView() {
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
                        final /* synthetic */ LoginActivity this$0;
                        /* access modifiers changed from: private */
                        public Runnable timeRunnable;
                        private int waitTime;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityResetWaitView(im.bclpbkiauv.ui.LoginActivity r20, android.content.Context r21) {
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
                                if (r7 == 0) goto L_0x0031
                                r7 = 5
                                goto L_0x0032
                            L_0x0031:
                                r7 = 3
                            L_0x0032:
                                r4.setGravity(r7)
                                android.widget.TextView r4 = r0.confirmTextView
                                r7 = 1073741824(0x40000000, float:2.0)
                                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r10 = (float) r10
                                r11 = 1065353216(0x3f800000, float:1.0)
                                r4.setLineSpacing(r10, r11)
                                android.widget.TextView r4 = r0.confirmTextView
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x004b
                                r10 = 5
                                goto L_0x004c
                            L_0x004b:
                                r10 = 3
                            L_0x004c:
                                r12 = -2
                                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                                r0.addView(r4, r10)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.resetAccountText = r4
                                boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r10 == 0) goto L_0x0061
                                r10 = 5
                                goto L_0x0062
                            L_0x0061:
                                r10 = 3
                            L_0x0062:
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
                                if (r10 == 0) goto L_0x0097
                                r10 = 5
                                goto L_0x0098
                            L_0x0097:
                                r10 = 3
                            L_0x0098:
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
                                if (r10 == 0) goto L_0x00b5
                                r10 = 5
                                goto L_0x00b6
                            L_0x00b5:
                                r10 = 3
                            L_0x00b6:
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
                                if (r5 == 0) goto L_0x00dd
                                r5 = 5
                                goto L_0x00de
                            L_0x00dd:
                                r5 = 3
                            L_0x00de:
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
                                if (r5 == 0) goto L_0x00fb
                                r5 = 5
                                goto L_0x00fc
                            L_0x00fb:
                                r5 = 3
                            L_0x00fc:
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
                                if (r4 == 0) goto L_0x013c
                                goto L_0x013d
                            L_0x013c:
                                r8 = 3
                            L_0x013d:
                                r12 = r8 | 48
                                r13 = 0
                                r14 = 7
                                r15 = 0
                                r16 = 0
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                                r0.addView(r3, r4)
                                android.widget.TextView r3 = r0.resetAccountButton
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityResetWaitView$RBA-X7FDP3Gkl_lbYjLDMHcd8bc r4 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityResetWaitView$RBA-X7FDP3Gkl_lbYjLDMHcd8bc
                                r4.<init>()
                                r3.setOnClickListener(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivityResetWaitView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ void lambda$new$3$LoginActivity$LoginActivityResetWaitView(View view) {
                            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        LoginActivity.LoginActivityResetWaitView.this.lambda$null$2$LoginActivity$LoginActivityResetWaitView(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder.create());
                            }
                        }

                        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityResetWaitView(DialogInterface dialogInterface, int i) {
                            this.this$0.needShowProgress(0);
                            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
                            req.reason = "Forgot password";
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    LoginActivity.LoginActivityResetWaitView.this.lambda$null$1$LoginActivity$LoginActivityResetWaitView(tLObject, tL_error);
                                }
                            }, 10);
                        }

                        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityResetWaitView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(error) {
                                private final /* synthetic */ TLRPC.TL_error f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityResetWaitView.this.lambda$null$0$LoginActivity$LoginActivityResetWaitView(this.f$1);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$0$LoginActivity$LoginActivityResetWaitView(TLRPC.TL_error error) {
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
                        final /* synthetic */ LoginActivity this$0;

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityRecoverView(im.bclpbkiauv.ui.LoginActivity r19, android.content.Context r20) {
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
                                if (r6 == 0) goto L_0x0031
                                r6 = 5
                                goto L_0x0032
                            L_0x0031:
                                r6 = 3
                            L_0x0032:
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
                                if (r9 == 0) goto L_0x0059
                                r9 = 5
                                goto L_0x005a
                            L_0x0059:
                                r9 = 3
                            L_0x005a:
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
                                if (r11 == 0) goto L_0x00e8
                                r11 = 5
                                goto L_0x00e9
                            L_0x00e8:
                                r11 = 3
                            L_0x00e9:
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
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRecoverView$ZflHKg_gAF872GdI9rRD5Yumy2E r11 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRecoverView$ZflHKg_gAF872GdI9rRD5Yumy2E
                                r11.<init>()
                                r4.setOnEditorActionListener(r11)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r1)
                                r0.cancelButton = r4
                                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r11 == 0) goto L_0x0117
                                r11 = 5
                                goto L_0x0118
                            L_0x0117:
                                r11 = 3
                            L_0x0118:
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
                                if (r4 == 0) goto L_0x014a
                                goto L_0x014b
                            L_0x014a:
                                r7 = 3
                            L_0x014b:
                                r11 = r7 | 80
                                r12 = 0
                                r13 = 0
                                r14 = 0
                                r15 = 14
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                                r0.addView(r3, r4)
                                android.widget.TextView r3 = r0.cancelButton
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRecoverView$ICDJcIbaBaMmyOK6qcWXazk0kbU r4 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRecoverView$ICDJcIbaBaMmyOK6qcWXazk0kbU
                                r4.<init>()
                                r3.setOnClickListener(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivityRecoverView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityRecoverView(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$2$LoginActivity$LoginActivityRecoverView(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
                            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    LoginActivity.LoginActivityRecoverView.this.lambda$null$1$LoginActivity$LoginActivityRecoverView(dialogInterface, i);
                                }
                            });
                            Dialog dialog = this.this$0.showDialog(builder.create());
                            if (dialog != null) {
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                            }
                        }

                        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRecoverView(DialogInterface dialogInterface, int i) {
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
                                        LoginActivity.LoginActivityRecoverView.this.lambda$onNextPressed$5$LoginActivity$LoginActivityRecoverView(tLObject, tL_error);
                                    }
                                }, 10);
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$5$LoginActivity$LoginActivityRecoverView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(response, error) {
                                private final /* synthetic */ TLObject f$1;
                                private final /* synthetic */ TLRPC.TL_error f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityRecoverView.this.lambda$null$4$LoginActivity$LoginActivityRecoverView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRecoverView(TLObject response, TLRPC.TL_error error) {
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
                                        LoginActivity.LoginActivityRecoverView.this.lambda$null$3$LoginActivity$LoginActivityRecoverView(this.f$1, dialogInterface, i);
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

                        public /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityRecoverView(TLObject response, DialogInterface dialogInterface, int i) {
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
                                    LoginActivity.LoginActivityRecoverView.this.lambda$onShow$6$LoginActivity$LoginActivityRecoverView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$6$LoginActivity$LoginActivityRecoverView() {
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
                        public AppCompatImageView avatarEditor;
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
                        final /* synthetic */ LoginActivity this$0;
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
                                            LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$0$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$3$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
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

                        public /* synthetic */ void lambda$showTermsOfService$0$LoginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
                            this.this$0.currentTermsOfService.popup = false;
                            onNextPressed();
                        }

                        public /* synthetic */ void lambda$showTermsOfService$3$LoginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder1.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
                            builder1.setMessage(LocaleController.getString("TosDecline", R.string.TosDecline));
                            builder1.setPositiveButton(LocaleController.getString("SignUp", R.string.SignUp), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    LoginActivity.LoginActivityRegisterView.this.lambda$null$1$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
                                }
                            });
                            builder1.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    LoginActivity.LoginActivityRegisterView.this.lambda$null$2$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
                                }
                            });
                            this.this$0.showDialog(builder1.create());
                        }

                        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRegisterView(DialogInterface dialog1, int which1) {
                            this.this$0.currentTermsOfService.popup = false;
                            onNextPressed();
                        }

                        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityRegisterView(DialogInterface dialog12, int which12) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }

                        /* JADX WARNING: Illegal instructions before constructor call */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public LoginActivityRegisterView(im.bclpbkiauv.ui.LoginActivity r37, android.content.Context r38) {
                            /*
                                r36 = this;
                                r0 = r36
                                r1 = r37
                                r2 = r38
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
                                if (r7 == 0) goto L_0x0051
                                r7 = 5
                                goto L_0x0052
                            L_0x0051:
                                r7 = 3
                            L_0x0052:
                                r5.setGravity(r7)
                                android.widget.TextView r5 = r0.textView
                                r7 = 1096810496(0x41600000, float:14.0)
                                r5.setTextSize(r4, r7)
                                android.widget.TextView r5 = r0.textView
                                r10 = -2
                                r11 = -2
                                boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r12 == 0) goto L_0x0066
                                r12 = 5
                                goto L_0x0067
                            L_0x0066:
                                r12 = 3
                            L_0x0067:
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
                                im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$1 r10 = new im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$1
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
                                if (r13 == 0) goto L_0x00b6
                                r13 = 5
                                goto L_0x00b7
                            L_0x00b6:
                                r13 = 3
                            L_0x00b7:
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
                                im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$2 r11 = new im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$2
                                r11.<init>(r2, r1, r10)
                                r0.avatarOverlay = r11
                                r12 = 1115684864(0x42800000, float:64.0)
                                r13 = 1115684864(0x42800000, float:64.0)
                                boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r14 == 0) goto L_0x00e2
                                r14 = 5
                                goto L_0x00e3
                            L_0x00e2:
                                r14 = 3
                            L_0x00e3:
                                r14 = r14 | 48
                                r15 = 0
                                r16 = 1098907648(0x41800000, float:16.0)
                                r17 = 0
                                r18 = 0
                                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                                r5.addView(r11, r12)
                                android.view.View r11 = r0.avatarOverlay
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$1FQnXgR32RzC9ZtKDGWgt4Qgulw r12 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$1FQnXgR32RzC9ZtKDGWgt4Qgulw
                                r12.<init>()
                                r11.setOnClickListener(r12)
                                im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$3 r11 = new im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$3
                                r11.<init>(r2, r1)
                                r0.avatarEditor = r11
                                android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
                                r11.setScaleType(r12)
                                androidx.appcompat.widget.AppCompatImageView r11 = r0.avatarEditor
                                r12 = 2131230820(0x7f080064, float:1.8077704E38)
                                r11.setImageResource(r12)
                                androidx.appcompat.widget.AppCompatImageView r11 = r0.avatarEditor
                                r11.setEnabled(r3)
                                androidx.appcompat.widget.AppCompatImageView r11 = r0.avatarEditor
                                r11.setClickable(r3)
                                androidx.appcompat.widget.AppCompatImageView r11 = r0.avatarEditor
                                r12 = 1073741824(0x40000000, float:2.0)
                                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                                r11.setPadding(r13, r3, r3, r3)
                                androidx.appcompat.widget.AppCompatImageView r11 = r0.avatarEditor
                                r13 = 1115684864(0x42800000, float:64.0)
                                r14 = 1115684864(0x42800000, float:64.0)
                                boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r15 == 0) goto L_0x0132
                                r15 = 5
                                goto L_0x0133
                            L_0x0132:
                                r15 = 3
                            L_0x0133:
                                r15 = r15 | 48
                                r16 = 0
                                r17 = 1098907648(0x41800000, float:16.0)
                                r18 = 0
                                r19 = 0
                                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                                r5.addView(r11, r13)
                                im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$4 r11 = new im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$4
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
                                if (r16 == 0) goto L_0x0167
                                r16 = 5
                                goto L_0x0169
                            L_0x0167:
                                r16 = 3
                            L_0x0169:
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
                                r13 = 2131692122(0x7f0f0a5a, float:1.9013335E38)
                                java.lang.String r12 = "Name2"
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
                                android.text.InputFilter[] r7 = new android.text.InputFilter[r4]
                                android.text.InputFilter$LengthFilter r13 = new android.text.InputFilter$LengthFilter
                                r4 = 64
                                r13.<init>(r4)
                                r7[r3] = r13
                                r8.setFilters(r7)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.firstNameField
                                r21 = -1082130432(0xffffffffbf800000, float:-1.0)
                                r22 = 1108344832(0x42100000, float:36.0)
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x0203
                                r8 = 5
                                goto L_0x0204
                            L_0x0203:
                                r8 = 3
                            L_0x0204:
                                r23 = r8 | 48
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                r28 = 1118437376(0x42aa0000, float:85.0)
                                if (r8 == 0) goto L_0x020f
                                r24 = 0
                                goto L_0x0211
                            L_0x020f:
                                r24 = 1118437376(0x42aa0000, float:85.0)
                            L_0x0211:
                                r25 = 0
                                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r8 == 0) goto L_0x021a
                                r26 = 1118437376(0x42aa0000, float:85.0)
                                goto L_0x021c
                            L_0x021a:
                                r26 = 0
                            L_0x021c:
                                r27 = 0
                                android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
                                r5.addView(r7, r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.firstNameField
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$Hlv3sU_yxj7oHZzuDH4Gax8QARE r8 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$Hlv3sU_yxj7oHZzuDH4Gax8QARE
                                r8.<init>()
                                r7.setOnEditorActionListener(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                                r7.<init>(r2)
                                r0.lastNameField = r7
                                r8 = 2131692181(0x7f0f0a95, float:1.9013455E38)
                                java.lang.String r13 = "Nickname"
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
                                r8 = 1
                                r7.setTextSize(r8, r12)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r7.setMaxLines(r8)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                r9 = 8192(0x2000, float:1.14794E-41)
                                r7.setInputType(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r0.lastNameField
                                android.text.InputFilter[] r9 = new android.text.InputFilter[r8]
                                android.text.InputFilter$LengthFilter r8 = new android.text.InputFilter$LengthFilter
                                r8.<init>(r4)
                                r9[r3] = r8
                                r7.setFilters(r9)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.lastNameField
                                r29 = -1082130432(0xffffffffbf800000, float:-1.0)
                                r30 = 1108344832(0x42100000, float:36.0)
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r7 == 0) goto L_0x02a8
                                r7 = 5
                                goto L_0x02a9
                            L_0x02a8:
                                r7 = 3
                            L_0x02a9:
                                r31 = r7 | 48
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r7 == 0) goto L_0x02b2
                                r32 = 0
                                goto L_0x02b4
                            L_0x02b2:
                                r32 = 1118437376(0x42aa0000, float:85.0)
                            L_0x02b4:
                                r33 = 1112276992(0x424c0000, float:51.0)
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r7 == 0) goto L_0x02bd
                                r34 = 1118437376(0x42aa0000, float:85.0)
                                goto L_0x02bf
                            L_0x02bd:
                                r34 = 0
                            L_0x02bf:
                                r35 = 0
                                android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
                                r5.addView(r4, r7)
                                im.bclpbkiauv.ui.components.EditTextBoldCursor r4 = r0.lastNameField
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$OMQVlW8LRch-STLnDx1t-hOzUss r7 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$OMQVlW8LRch-STLnDx1t-hOzUss
                                r7.<init>()
                                r4.setOnEditorActionListener(r7)
                                android.widget.TextView r4 = new android.widget.TextView
                                r4.<init>(r2)
                                r0.wrongNumber = r4
                                r7 = 2131690322(0x7f0f0352, float:1.9009684E38)
                                java.lang.String r8 = "CancelRegistration"
                                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
                                r4.setText(r7)
                                android.widget.TextView r4 = r0.wrongNumber
                                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r7 == 0) goto L_0x02ed
                                r7 = 5
                                goto L_0x02ee
                            L_0x02ed:
                                r7 = 3
                            L_0x02ee:
                                r8 = 1
                                r7 = r7 | r8
                                r4.setGravity(r7)
                                android.widget.TextView r4 = r0.wrongNumber
                                java.lang.String r7 = "windowBackgroundWhiteBlueText4"
                                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                                r4.setTextColor(r7)
                                android.widget.TextView r4 = r0.wrongNumber
                                r7 = 1096810496(0x41600000, float:14.0)
                                r4.setTextSize(r8, r7)
                                android.widget.TextView r4 = r0.wrongNumber
                                r7 = 1073741824(0x40000000, float:2.0)
                                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                float r7 = (float) r8
                                r8 = 1065353216(0x3f800000, float:1.0)
                                r4.setLineSpacing(r7, r8)
                                android.widget.TextView r4 = r0.wrongNumber
                                r7 = 1103101952(0x41c00000, float:24.0)
                                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                                r4.setPadding(r3, r7, r3, r3)
                                android.widget.TextView r3 = r0.wrongNumber
                                r4 = 8
                                r3.setVisibility(r4)
                                android.widget.TextView r3 = r0.wrongNumber
                                r21 = -2
                                r22 = -2
                                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                                if (r4 == 0) goto L_0x0333
                                r16 = 5
                                goto L_0x0335
                            L_0x0333:
                                r16 = 3
                            L_0x0335:
                                r23 = r16 | 48
                                r24 = 0
                                r25 = 20
                                r26 = 0
                                r27 = 0
                                android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                                r0.addView(r3, r4)
                                android.widget.TextView r3 = r0.wrongNumber
                                im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$1MJfpFCvQTjuxVwTmmmJTE9hoR0 r4 = new im.bclpbkiauv.ui.-$$Lambda$LoginActivity$LoginActivityRegisterView$1MJfpFCvQTjuxVwTmmmJTE9hoR0
                                r4.<init>()
                                r3.setOnClickListener(r4)
                                android.widget.TextView r3 = new android.widget.TextView
                                r3.<init>(r2)
                                r0.privacyView = r3
                                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                                r3.setTextColor(r4)
                                android.widget.TextView r3 = r0.privacyView
                                im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy r4 = new im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy
                                r4.<init>()
                                r3.setMovementMethod(r4)
                                android.widget.TextView r3 = r0.privacyView
                                java.lang.String r4 = "windowBackgroundWhiteLinkText"
                                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
                                r3.setLinkTextColor(r4)
                                android.widget.TextView r3 = r0.privacyView
                                r4 = 1096810496(0x41600000, float:14.0)
                                r6 = 1
                                r3.setTextSize(r6, r4)
                                android.widget.TextView r3 = r0.privacyView
                                r4 = 81
                                r3.setGravity(r4)
                                android.widget.TextView r3 = r0.privacyView
                                r4 = 1073741824(0x40000000, float:2.0)
                                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                                float r4 = (float) r4
                                r3.setLineSpacing(r4, r8)
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
                                if (r7 == r8) goto L_0x03da
                                if (r6 == r8) goto L_0x03da
                                if (r7 == r6) goto L_0x03da
                                int r8 = r6 + 1
                                java.lang.String r9 = ""
                                r4.replace(r6, r8, r9)
                                int r8 = r7 + 1
                                r4.replace(r7, r8, r9)
                                im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$LinkSpan r8 = new im.bclpbkiauv.ui.LoginActivity$LoginActivityRegisterView$LinkSpan
                                r8.<init>()
                                int r9 = r6 + -1
                                r11 = 33
                                r4.setSpan(r8, r7, r9, r11)
                            L_0x03da:
                                android.widget.TextView r8 = r0.privacyView
                                r8.setText(r4)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LoginActivity.LoginActivityRegisterView.<init>(im.bclpbkiauv.ui.LoginActivity, android.content.Context):void");
                        }

                        public /* synthetic */ void lambda$new$5$LoginActivity$LoginActivityRegisterView(View view) {
                            this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
                                public final void run() {
                                    LoginActivity.LoginActivityRegisterView.this.lambda$null$4$LoginActivity$LoginActivityRegisterView();
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRegisterView() {
                            this.avatar = null;
                            this.avatarBig = null;
                            this.uploadedAvatar = null;
                            showAvatarProgress(false, true);
                            this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
                            this.avatarEditor.setImageResource(R.drawable.actions_setphoto);
                        }

                        public /* synthetic */ boolean lambda$new$6$LoginActivity$LoginActivityRegisterView(TextView textView2, int i, KeyEvent keyEvent) {
                            if (i != 5) {
                                return false;
                            }
                            this.lastNameField.requestFocus();
                            return true;
                        }

                        public /* synthetic */ boolean lambda$new$7$LoginActivity$LoginActivityRegisterView(TextView textView2, int i, KeyEvent keyEvent) {
                            if (i != 6 && i != 5) {
                                return false;
                            }
                            onNextPressed();
                            return true;
                        }

                        public /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityRegisterView(View view) {
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
                                    LoginActivity.LoginActivityRegisterView.this.lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
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
                                        LoginActivity.LoginActivityRegisterView.this.lambda$onBackPressed$10$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
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

                        public /* synthetic */ void lambda$onBackPressed$10$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
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
                                    LoginActivity.LoginActivityRegisterView.this.lambda$onShow$11$LoginActivity$LoginActivityRegisterView();
                                }
                            }, 100);
                        }

                        public /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivityRegisterView() {
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
                                            LoginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$13$LoginActivity$LoginActivityRegisterView(tLObject, tL_error);
                                        }
                                    }, 10);
                                    return;
                                }
                                showTermsOfService(true);
                            }
                        }

                        public /* synthetic */ void lambda$onNextPressed$13$LoginActivity$LoginActivityRegisterView(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable(response, error) {
                                private final /* synthetic */ TLObject f$1;
                                private final /* synthetic */ TLRPC.TL_error f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    LoginActivity.LoginActivityRegisterView.this.lambda$null$12$LoginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityRegisterView(TLObject response, TLRPC.TL_error error) {
                            this.nextPressed = false;
                            this.this$0.needHideProgress(false);
                            if (response instanceof TLRPC.TL_auth_authorization) {
                                this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                                if (this.avatarBig != null) {
                                    MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(this.avatarBig);
                                }
                            } else if (TextUtils.isEmpty(this.firstNameField.getText().toString().trim())) {
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
