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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChangePhoneActivity;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.HintEditText;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.SlideView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import kotlin.text.Typography;
import org.slf4j.Marker;

@Deprecated
public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    /* access modifiers changed from: private */
    public int currentViewNum = 0;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int scrollHeight;
    /* access modifiers changed from: private */
    public SlideView[] views = new SlideView[5];

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

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[a] != null) {
                slideViewArr[a].onDestroyActivity();
            }
            a++;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
                } else if (id == -1) {
                    ChangePhoneActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        ScrollView scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (ChangePhoneActivity.this.currentViewNum == 1 || ChangePhoneActivity.this.currentViewNum == 2 || ChangePhoneActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = ChangePhoneActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
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
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a < slideViewArr.length) {
                slideViewArr[a].setVisibility(a == 0 ? 0 : 8);
                frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1.0f, a == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
                a++;
            } else {
                this.actionBar.setTitle(this.views[0].getHeaderName());
                return this.fragmentView;
            }
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            int i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
    }

    public boolean onBackPressed() {
        if (progressDialogIsShown()) {
            needHideProgress();
        }
        int i = this.currentViewNum;
        if (i == 0) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a >= slideViewArr.length) {
                    return true;
                }
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            }
        } else {
            if (this.views[i].onBackPressed(false)) {
                setPage(0, true, (Bundle) null, true);
            }
            return false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AnonymousClass3 r0 = new AlertDialog(getParentActivity(), 3) {
                public void onBackPressed() {
                    ChangePhoneActivity.this.progressDialog.cancel();
                }
            };
            this.progressDialog = r0;
            r0.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    ChangePhoneActivity.this.lambda$needShowProgress$0$ChangePhoneActivity(dialogInterface);
                }
            });
            this.progressDialog.setCancelable(true);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.show();
        }
    }

    public /* synthetic */ void lambda$needShowProgress$0$ChangePhoneActivity(DialogInterface dialog) {
        int i;
        SlideView[] slideViewArr = this.views;
        if (slideViewArr != null && (i = this.currentViewNum) >= 0 && i < slideViewArr.length && (slideViewArr[i] instanceof PhoneView)) {
            boolean unused = ((PhoneView) slideViewArr[i]).cancelNetRequest();
        }
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    private boolean progressDialogIsShown() {
        AlertDialog alertDialog = this.progressDialog;
        return alertDialog != null && alertDialog.isShowing();
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (page == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView outView = slideViewArr[this.currentViewNum];
        final SlideView newView = slideViewArr[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        this.actionBar.setTitle(newView.getHeaderName());
        newView.onShow();
        int i = AndroidUtilities.displaySize.x;
        if (back) {
            i = -i;
        }
        newView.setX((float) i);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        float[] fArr = new float[1];
        int i2 = AndroidUtilities.displaySize.x;
        if (!back) {
            i2 = -i2;
        }
        fArr[0] = (float) i2;
        animatorArr[0] = ObjectAnimator.ofFloat(outView, "translationX", fArr);
        animatorArr[1] = ObjectAnimator.ofFloat(newView, "translationX", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                newView.setVisibility(0);
            }

            public void onAnimationEnd(Animator animation) {
                outView.setVisibility(8);
                outView.setX(0.0f);
            }
        });
        animatorSet.start();
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
        private int netRequestToken = -1;
        private boolean nextPressed = false;
        /* access modifiers changed from: private */
        public HintEditText phoneField;
        /* access modifiers changed from: private */
        public HashMap<String, String> phoneFormatMap = new HashMap<>();
        /* access modifiers changed from: private */
        public TextView textView;
        /* access modifiers changed from: private */
        public TextView textView2;
        final /* synthetic */ ChangePhoneActivity this$0;
        /* access modifiers changed from: private */
        public View view;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(im.bclpbkiauv.ui.ChangePhoneActivity r31, android.content.Context r32) {
            /*
                r30 = this;
                r1 = r30
                r2 = r31
                r3 = r32
                r1.this$0 = r2
                r1.<init>(r3)
                r4 = 0
                r1.countryState = r4
                r5 = -1
                r1.netRequestToken = r5
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r1.countriesArray = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.countriesMap = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.codesMap = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.phoneFormatMap = r0
                r1.ignoreSelection = r4
                r1.ignoreOnTextChange = r4
                r1.ignoreOnPhoneChange = r4
                r1.nextPressed = r4
                r6 = 1
                r1.setOrientation(r6)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.countryButton = r0
                r7 = 1099956224(0x41900000, float:18.0)
                r0.setTextSize(r6, r7)
                android.widget.TextView r0 = r1.countryButton
                r8 = 1094713344(0x41400000, float:12.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                r10 = 1092616192(0x41200000, float:10.0)
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                r0.setPadding(r9, r11, r12, r4)
                android.widget.TextView r0 = r1.countryButton
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setTextColor(r11)
                android.widget.TextView r0 = r1.countryButton
                r0.setMaxLines(r6)
                android.widget.TextView r0 = r1.countryButton
                r0.setSingleLine(r6)
                android.widget.TextView r0 = r1.countryButton
                android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END
                r0.setEllipsize(r11)
                android.widget.TextView r0 = r1.countryButton
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r12 = 5
                r13 = 3
                if (r11 == 0) goto L_0x0081
                r11 = 5
                goto L_0x0082
            L_0x0081:
                r11 = 3
            L_0x0082:
                r11 = r11 | r6
                r0.setGravity(r11)
                android.widget.TextView r0 = r1.countryButton
                r11 = 2131231608(0x7f080378, float:1.8079302E38)
                r0.setBackgroundResource(r11)
                android.widget.TextView r0 = r1.countryButton
                r14 = -1
                r15 = 36
                r16 = 0
                r17 = 0
                r18 = 0
                r19 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r15, (float) r16, (float) r17, (float) r18, (float) r19)
                r1.addView(r0, r11)
                android.widget.TextView r0 = r1.countryButton
                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$vcsVGobI6tQpZt6_M8XJe4WXJfk r11 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$vcsVGobI6tQpZt6_M8XJe4WXJfk
                r11.<init>()
                r0.setOnClickListener(r11)
                android.view.View r0 = new android.view.View
                r0.<init>(r3)
                r1.view = r0
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                r0.setPadding(r11, r4, r8, r4)
                android.view.View r0 = r1.view
                java.lang.String r8 = "windowBackgroundWhiteGrayLine"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r0.setBackgroundColor(r8)
                android.view.View r0 = r1.view
                r15 = 1
                r16 = 1082130432(0x40800000, float:4.0)
                r17 = -1047789568(0xffffffffc18c0000, float:-17.5)
                r18 = 1082130432(0x40800000, float:4.0)
                r19 = 0
                android.widget.LinearLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r15, (float) r16, (float) r17, (float) r18, (float) r19)
                r1.addView(r0, r8)
                android.widget.LinearLayout r0 = new android.widget.LinearLayout
                r0.<init>(r3)
                r8 = r0
                r8.setOrientation(r4)
                r15 = -2
                r16 = 0
                r17 = 1101004800(0x41a00000, float:20.0)
                r18 = 0
                android.widget.LinearLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r15, (float) r16, (float) r17, (float) r18, (float) r19)
                r1.addView(r8, r0)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.textView = r0
                java.lang.String r11 = "+"
                r0.setText(r11)
                android.widget.TextView r0 = r1.textView
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setTextColor(r11)
                android.widget.TextView r0 = r1.textView
                r0.setTextSize(r6, r7)
                android.widget.TextView r0 = r1.textView
                r11 = -2
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r11)
                r8.addView(r0, r11)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                r0.<init>(r3)
                r1.codeField = r0
                r0.setInputType(r13)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setTextColor(r11)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setCursorColor(r11)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r11 = 1101004800(0x41a00000, float:20.0)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                r0.setCursorSize(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r14 = 1069547520(0x3fc00000, float:1.5)
                r0.setCursorWidth(r14)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                android.graphics.drawable.Drawable r15 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r3, r4)
                r0.setBackgroundDrawable(r15)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
                r0.setPadding(r10, r4, r4, r4)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.setTextSize(r6, r7)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.setMaxLines(r6)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r10 = 19
                r0.setGravity(r10)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r15 = 268435461(0x10000005, float:2.5243564E-29)
                r0.setImeOptions(r15)
                android.text.InputFilter[] r5 = new android.text.InputFilter[r6]
                android.text.InputFilter$LengthFilter r0 = new android.text.InputFilter$LengthFilter
                r0.<init>(r12)
                r5[r4] = r0
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.setFilters(r5)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r17 = 55
                r18 = 36
                r19 = -1055916032(0xffffffffc1100000, float:-9.0)
                r20 = 0
                r21 = 1098907648(0x41800000, float:16.0)
                r22 = 0
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
                r8.addView(r0, r12)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                im.bclpbkiauv.ui.ChangePhoneActivity$PhoneView$1 r12 = new im.bclpbkiauv.ui.ChangePhoneActivity$PhoneView$1
                r12.<init>(r2)
                r0.addTextChangedListener(r12)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$96YOT0hFkAhU-ZU_P5dtXth-g44 r12 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$96YOT0hFkAhU-ZU_P5dtXth-g44
                r12.<init>()
                r0.setOnEditorActionListener(r12)
                im.bclpbkiauv.ui.components.HintEditText r0 = new im.bclpbkiauv.ui.components.HintEditText
                r0.<init>(r3)
                r1.phoneField = r0
                r0.setInputType(r13)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setTextColor(r12)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                java.lang.String r12 = "windowBackgroundWhiteHintText"
                int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
                r0.setHintTextColor(r12)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                android.graphics.drawable.Drawable r12 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r3, r4)
                r0.setBackgroundDrawable(r12)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setPadding(r4, r4, r4, r4)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r0.setCursorColor(r9)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
                r0.setCursorSize(r9)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setCursorWidth(r14)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setTextSize(r6, r7)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setMaxLines(r6)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setGravity(r10)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.setImeOptions(r15)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r7 = 1108344832(0x42100000, float:36.0)
                r9 = -1
                android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r9, r7)
                r8.addView(r0, r7)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                im.bclpbkiauv.ui.ChangePhoneActivity$PhoneView$2 r7 = new im.bclpbkiauv.ui.ChangePhoneActivity$PhoneView$2
                r7.<init>(r2)
                r0.addTextChangedListener(r7)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$eRVA4Z5P267wxU3JAywEuHpvKSY r7 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$eRVA4Z5P267wxU3JAywEuHpvKSY
                r7.<init>()
                r0.setOnEditorActionListener(r7)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$6lgY-xa9A85S8Z6MMgEG2PFVxD8 r7 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$PhoneView$6lgY-xa9A85S8Z6MMgEG2PFVxD8
                r7.<init>()
                r0.setOnKeyListener(r7)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.textView2 = r0
                r7 = 2131690383(0x7f0f038f, float:1.9009808E38)
                java.lang.String r9 = "ChangePhoneHelp"
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
                r0.setText(r7)
                android.widget.TextView r0 = r1.textView2
                java.lang.String r7 = "windowBackgroundWhiteGrayText6"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r0.setTextColor(r7)
                android.widget.TextView r0 = r1.textView2
                r7 = 1096810496(0x41600000, float:14.0)
                r0.setTextSize(r6, r7)
                android.widget.TextView r0 = r1.textView2
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0257
                r7 = 5
                goto L_0x0258
            L_0x0257:
                r7 = 3
            L_0x0258:
                r0.setGravity(r7)
                android.widget.TextView r0 = r1.textView2
                r7 = 1073741824(0x40000000, float:2.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                r9 = 1065353216(0x3f800000, float:1.0)
                r0.setLineSpacing(r7, r9)
                android.widget.TextView r0 = r1.textView2
                r23 = -2
                r24 = -2
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0276
                r25 = 5
                goto L_0x0278
            L_0x0276:
                r25 = 3
            L_0x0278:
                r26 = 0
                r27 = 28
                r28 = 0
                r29 = 10
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r1.addView(r0, r7)
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r7 = r0
                java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x02e5 }
                java.io.InputStreamReader r9 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x02e5 }
                android.content.res.Resources r10 = r30.getResources()     // Catch:{ Exception -> 0x02e5 }
                android.content.res.AssetManager r10 = r10.getAssets()     // Catch:{ Exception -> 0x02e5 }
                java.lang.String r11 = "countries.txt"
                java.io.InputStream r10 = r10.open(r11)     // Catch:{ Exception -> 0x02e5 }
                r9.<init>(r10)     // Catch:{ Exception -> 0x02e5 }
                r0.<init>(r9)     // Catch:{ Exception -> 0x02e5 }
            L_0x02a5:
                java.lang.String r9 = r0.readLine()     // Catch:{ Exception -> 0x02e5 }
                r10 = r9
                if (r9 == 0) goto L_0x02e1
                java.lang.String r9 = ";"
                java.lang.String[] r9 = r10.split(r9)     // Catch:{ Exception -> 0x02e5 }
                java.util.ArrayList<java.lang.String> r11 = r1.countriesArray     // Catch:{ Exception -> 0x02e5 }
                r12 = 2
                r14 = r9[r12]     // Catch:{ Exception -> 0x02e5 }
                r11.add(r4, r14)     // Catch:{ Exception -> 0x02e5 }
                java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.countriesMap     // Catch:{ Exception -> 0x02e5 }
                r14 = r9[r12]     // Catch:{ Exception -> 0x02e5 }
                r15 = r9[r4]     // Catch:{ Exception -> 0x02e5 }
                r11.put(r14, r15)     // Catch:{ Exception -> 0x02e5 }
                java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.codesMap     // Catch:{ Exception -> 0x02e5 }
                r14 = r9[r4]     // Catch:{ Exception -> 0x02e5 }
                r15 = r9[r12]     // Catch:{ Exception -> 0x02e5 }
                r11.put(r14, r15)     // Catch:{ Exception -> 0x02e5 }
                int r11 = r9.length     // Catch:{ Exception -> 0x02e5 }
                if (r11 <= r13) goto L_0x02d8
                java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.phoneFormatMap     // Catch:{ Exception -> 0x02e5 }
                r14 = r9[r4]     // Catch:{ Exception -> 0x02e5 }
                r15 = r9[r13]     // Catch:{ Exception -> 0x02e5 }
                r11.put(r14, r15)     // Catch:{ Exception -> 0x02e5 }
            L_0x02d8:
                r11 = r9[r6]     // Catch:{ Exception -> 0x02e5 }
                r12 = r9[r12]     // Catch:{ Exception -> 0x02e5 }
                r7.put(r11, r12)     // Catch:{ Exception -> 0x02e5 }
                goto L_0x02a5
            L_0x02e1:
                r0.close()     // Catch:{ Exception -> 0x02e5 }
                goto L_0x02e9
            L_0x02e5:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x02e9:
                java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
                im.bclpbkiauv.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r9 = im.bclpbkiauv.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
                java.util.Collections.sort(r0, r9)
                r9 = 0
                android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0307 }
                java.lang.String r10 = "phone"
                java.lang.Object r0 = r0.getSystemService(r10)     // Catch:{ Exception -> 0x0307 }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0307 }
                if (r0 == 0) goto L_0x0306
                java.lang.String r10 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0307 }
                java.lang.String r10 = r10.toUpperCase()     // Catch:{ Exception -> 0x0307 }
                r9 = r10
            L_0x0306:
                goto L_0x030b
            L_0x0307:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x030b:
                if (r9 == 0) goto L_0x032d
                java.lang.Object r0 = r7.get(r9)
                java.lang.String r0 = (java.lang.String) r0
                if (r0 == 0) goto L_0x032d
                java.util.ArrayList<java.lang.String> r10 = r1.countriesArray
                int r10 = r10.indexOf(r0)
                r11 = -1
                if (r10 == r11) goto L_0x032d
                im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r1.codeField
                java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.countriesMap
                java.lang.Object r12 = r12.get(r0)
                java.lang.CharSequence r12 = (java.lang.CharSequence) r12
                r11.setText(r12)
                r1.countryState = r4
            L_0x032d:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x034b
                android.widget.TextView r0 = r1.countryButton
                r4 = 2131690589(0x7f0f045d, float:1.9010226E38)
                java.lang.String r10 = "ChooseCountry"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r4)
                r0.setText(r4)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r4 = 0
                r0.setHintText(r4)
                r1.countryState = r6
            L_0x034b:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x0367
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                im.bclpbkiauv.messenger.AndroidUtilities.showKeyboard(r0)
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                r0.requestFocus()
                im.bclpbkiauv.ui.components.HintEditText r0 = r1.phoneField
                int r4 = r0.length()
                r0.setSelection(r4)
                goto L_0x0371
            L_0x0367:
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                im.bclpbkiauv.messenger.AndroidUtilities.showKeyboard(r0)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r1.codeField
                r0.requestFocus()
            L_0x0371:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChangePhoneActivity.PhoneView.<init>(im.bclpbkiauv.ui.ChangePhoneActivity, android.content.Context):void");
        }

        public /* synthetic */ void lambda$new$2$ChangePhoneActivity$PhoneView(View view2) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    ChangePhoneActivity.PhoneView.this.lambda$null$1$ChangePhoneActivity$PhoneView(country);
                }
            });
            this.this$0.presentFragment(fragment);
        }

        public /* synthetic */ void lambda$null$1$ChangePhoneActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry((String) null, country);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChangePhoneActivity.PhoneView.this.lambda$null$0$ChangePhoneActivity$PhoneView();
                }
            }, 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        public /* synthetic */ void lambda$null$0$ChangePhoneActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        public /* synthetic */ boolean lambda$new$3$ChangePhoneActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        public /* synthetic */ boolean lambda$new$4$ChangePhoneActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ boolean lambda$new$5$ChangePhoneActivity$PhoneView(View v, int keyCode, KeyEvent event) {
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

        public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed() {
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                if (this.countryState == 1) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                } else if (this.codeField.length() == 0) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else {
                    if (TextUtils.isEmpty(this.phoneField.getText() + "")) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                        return;
                    }
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    if (this.this$0.getUserConfig() == null || this.this$0.getUserConfig().getClientPhone() == null || !this.this$0.getUserConfig().getClientPhone().equals(phone)) {
                        TLRPC.TL_account_sendChangePhoneCode req = new TLRPC.TL_account_sendChangePhoneCode();
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
                        StringBuilder sb = new StringBuilder();
                        sb.append(PhoneFormat.stripExceptNumbers("" + this.codeField.getText()));
                        sb.append(" ");
                        sb.append(PhoneFormat.stripExceptNumbers("" + this.phoneField.getText()));
                        params.putString("phoneFormatedHasBlank", sb.toString());
                        this.nextPressed = true;
                        this.this$0.needShowProgress();
                        this.netRequestToken = ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(params, req) {
                            private final /* synthetic */ Bundle f$1;
                            private final /* synthetic */ TLRPC.TL_account_sendChangePhoneCode f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                ChangePhoneActivity.PhoneView.this.lambda$onNextPressed$7$ChangePhoneActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        }, 2);
                        return;
                    }
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("CannotMatchTheCurrentPhoneNumber", R.string.CannotMatchTheCurrentPhoneNumber));
                }
            }
        }

        public /* synthetic */ void lambda$onNextPressed$7$ChangePhoneActivity$PhoneView(Bundle params, TLRPC.TL_account_sendChangePhoneCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, params, response, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ Bundle f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPC.TL_account_sendChangePhoneCode f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ChangePhoneActivity.PhoneView.this.lambda$null$6$ChangePhoneActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$6$ChangePhoneActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_account_sendChangePhoneCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else {
                AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, params.getString("phone"));
            }
            this.this$0.needHideProgress();
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public String getHeaderName() {
            return LocaleController.getString("ChangePhoneNewNumber", R.string.ChangePhoneNewNumber);
        }

        public boolean onBackPressed(boolean force) {
            if (!force || !cancelNetRequest()) {
                return super.onBackPressed(force);
            }
            return true;
        }

        /* access modifiers changed from: private */
        public boolean cancelNetRequest() {
            if (this.netRequestToken == -1 || ConnectionsManager.getInstance(this.this$0.currentAccount) == null) {
                return false;
            }
            ConnectionsManager.getInstance(this.this$0.currentAccount).cancelRequest(this.netRequestToken, true);
            return true;
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        /* access modifiers changed from: private */
        public ImageView blackImageView;
        /* access modifiers changed from: private */
        public ImageView blueImageView;
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
        private int openTime;
        private String pattern = "*";
        private String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        private String requestHasBlankPhone;
        /* access modifiers changed from: private */
        public String requestPhone;
        final /* synthetic */ ChangePhoneActivity this$0;
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
        public LoginActivitySmsView(im.bclpbkiauv.ui.ChangePhoneActivity r31, android.content.Context r32, int r33) {
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
                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$1 r5 = new im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$1
                r5.<init>(r2, r1)
                r0.timeText = r5
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.timeText
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r10)
                int r5 = r0.currentType
                r6 = 1097859072(0x41700000, float:15.0)
                r9 = 1092616192(0x41200000, float:10.0)
                if (r5 != r13) goto L_0x0291
                android.widget.TextView r5 = r0.timeText
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0260
                r11 = 5
                goto L_0x0261
            L_0x0260:
                r11 = 3
            L_0x0261:
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r11)
                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r5 = new im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                android.widget.TextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0277
                r12 = 5
                goto L_0x0278
            L_0x0277:
                r12 = 3
            L_0x0278:
                r5.setGravity(r12)
                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r5 = r0.progressView
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
                android.widget.TextView r5 = r0.timeText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r11, r7, r12)
                android.widget.TextView r5 = r0.timeText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.timeText
                r11 = 49
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.timeText
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
            L_0x02b3:
                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$2 r5 = new im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$2
                r5.<init>(r2, r1)
                r0.problemText = r5
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.problemText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.problemText
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r8, r7, r9)
                android.widget.TextView r5 = r0.problemText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.problemText
                r6 = 49
                r5.setGravity(r6)
                int r5 = r0.currentType
                if (r5 != r4) goto L_0x02fa
                android.widget.TextView r4 = r0.problemText
                r5 = 2131690916(0x7f0f05a4, float:1.901089E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x0308
            L_0x02fa:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131690915(0x7f0f05a3, float:1.9010887E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x0308:
                android.widget.TextView r4 = r0.problemText
                r5 = 49
                android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.problemText
                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$iLnrstMNS55-BMwHCupgcIvt0Yw r5 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$iLnrstMNS55-BMwHCupgcIvt0Yw
                r5.<init>()
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.<init>(im.bclpbkiauv.ui.ChangePhoneActivity, android.content.Context, int):void");
        }

        public /* synthetic */ void lambda$new$0$ChangePhoneActivity$LoginActivitySmsView(View v) {
            if (!this.nextPressed) {
                if (!((this.nextType == 4 && this.currentType == 2) || this.nextType == 0)) {
                    resendCode();
                    return;
                }
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
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
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

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            params.putString("phoneFormatedHasBlank", this.requestHasBlankPhone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(params, req) {
                private final /* synthetic */ Bundle f$1;
                private final /* synthetic */ TLRPC.TL_auth_resendCode f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChangePhoneActivity.LoginActivitySmsView.this.lambda$resendCode$3$ChangePhoneActivity$LoginActivitySmsView(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 2);
        }

        public /* synthetic */ void lambda$resendCode$3$ChangePhoneActivity$LoginActivitySmsView(Bundle params, TLRPC.TL_auth_resendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, params, response, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ Bundle f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPC.TL_auth_resendCode f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ChangePhoneActivity.LoginActivitySmsView.this.lambda$null$2$ChangePhoneActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$ChangePhoneActivity$LoginActivitySmsView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChangePhoneActivity.LoginActivitySmsView.this.lambda$null$1$ChangePhoneActivity$LoginActivitySmsView(dialogInterface, i);
                        }
                    });
                }
            }
            this.this$0.needHideProgress();
        }

        public /* synthetic */ void lambda$null$1$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialog1, int which) {
            onBackPressed(true);
            this.this$0.finishFragment();
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

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
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
                this.requestHasBlankPhone = bundle.getString("phoneFormatedHasBlank");
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
                                return ChangePhoneActivity.LoginActivitySmsView.this.lambda$setParams$4$ChangePhoneActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
                            }
                        });
                        this.codeField[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                return ChangePhoneActivity.LoginActivitySmsView.this.lambda$setParams$5$ChangePhoneActivity$LoginActivitySmsView(textView, i, keyEvent);
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
                    } else if (i9 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i10 = this.nextType;
                        if (i10 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                        } else if (i10 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                        }
                        createTimer();
                    } else if (this.currentType == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView.setVisibility(i7);
                        createTimer();
                    } else if (this.currentType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView2 = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView2.setVisibility(i7);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$4$ChangePhoneActivity$LoginActivitySmsView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        public /* synthetic */ boolean lambda$setParams$5$ChangePhoneActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                              (wrap: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU) = 
                              (r1v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR)
                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.4.run():void, dex: classes2.dex
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
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU) = 
                              (r1v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$4):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.4.run():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU r0 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$4$xR4FAGXfL2rstpO6CBdxFZU8QGU
                            r0.<init>(r1)
                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass4.run():void");
                    }

                    public /* synthetic */ void lambda$run$0$ChangePhoneActivity$LoginActivitySmsView$4() {
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
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    double currentTime = (double) System.currentTimeMillis();
                                    int unused = LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                                    double unused2 = LoginActivitySmsView.this.lastCurrentTime = currentTime;
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
                                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x01e6: INVOKE  
                                                  (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x01dd: INVOKE  (r5v17 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                                  (wrap: int : 0x01d9: INVOKE  (r5v16 int) = 
                                                  (wrap: im.bclpbkiauv.ui.ChangePhoneActivity : 0x01d7: IGET  (r5v15 im.bclpbkiauv.ui.ChangePhoneActivity) = 
                                                  (wrap: im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView : 0x01d5: IGET  (r5v14 im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView) = 
                                                  (wrap: im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 : 0x01d3: IGET  (r5v13 im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5) = 
                                                  (r13v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1 A[THIS])
                                                 im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.1.this$2 im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5)
                                                 im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.this$1 im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView)
                                                 im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this$0 im.bclpbkiauv.ui.ChangePhoneActivity)
                                                 im.bclpbkiauv.ui.ChangePhoneActivity.access$4200(im.bclpbkiauv.ui.ChangePhoneActivity):int type: STATIC)
                                                 im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                                  (r4v26 'req' im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode)
                                                  (wrap: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ : 0x01e3: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ) = 
                                                  (r13v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1 A[THIS])
                                                 call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1):void type: CONSTRUCTOR)
                                                  (2 int)
                                                 im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate, int):int type: VIRTUAL in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.1.run():void, dex: classes2.dex
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
                                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x01e3: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ) = 
                                                  (r13v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1 A[THIS])
                                                 call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.1.run():void, dex: classes2.dex
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	... 138 more
                                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ, state: NOT_LOADED
                                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	... 144 more
                                                */
                                            /*
                                                this = this;
                                                long r0 = java.lang.System.currentTimeMillis()
                                                double r0 = (double) r0
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r2 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r2 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                double r2 = r2.lastCurrentTime
                                                double r2 = r0 - r2
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r5 = r5.time
                                                double r5 = (double) r5
                                                double r5 = r5 - r2
                                                int r5 = (int) r5
                                                int unused = r4.time = r5
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                double unused = r4.lastCurrentTime = r0
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.time
                                                r5 = 1065353216(0x3f800000, float:1.0)
                                                r6 = 3
                                                r7 = 1000(0x3e8, float:1.401E-42)
                                                r8 = 4
                                                r9 = 0
                                                r10 = 2
                                                if (r4 < r7) goto L_0x00e1
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.time
                                                int r4 = r4 / r7
                                                int r4 = r4 / 60
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r11 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r11 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r11 = r11.time
                                                int r11 = r11 / r7
                                                int r7 = r4 * 60
                                                int r11 = r11 - r7
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r7 = r7.nextType
                                                r12 = 1
                                                if (r7 == r8) goto L_0x0094
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r7 = r7.nextType
                                                if (r7 != r6) goto L_0x0067
                                                goto L_0x0094
                                            L_0x0067:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r6 = r6.nextType
                                                if (r6 != r10) goto L_0x00b6
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                android.widget.TextView r6 = r6.timeText
                                                r7 = 2131694001(0x7f0f11b1, float:1.9017146E38)
                                                java.lang.Object[] r8 = new java.lang.Object[r10]
                                                java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
                                                r8[r9] = r10
                                                java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                                                r8[r12] = r9
                                                java.lang.String r9 = "SmsText"
                                                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r9, r7, r8)
                                                r6.setText(r7)
                                                goto L_0x00b6
                                            L_0x0094:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                android.widget.TextView r6 = r6.timeText
                                                r7 = 2131690300(0x7f0f033c, float:1.900964E38)
                                                java.lang.Object[] r8 = new java.lang.Object[r10]
                                                java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
                                                r8[r9] = r10
                                                java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                                                r8[r12] = r9
                                                java.lang.String r9 = "CallText"
                                                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r9, r7, r8)
                                                r6.setText(r7)
                                            L_0x00b6:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r6 = r6.progressView
                                                if (r6 == 0) goto L_0x00df
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r6 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r6 = r6.progressView
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r7 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r7 = r7.time
                                                float r7 = (float) r7
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r8 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r8 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r8 = r8.timeout
                                                float r8 = (float) r8
                                                float r7 = r7 / r8
                                                float r5 = r5 - r7
                                                r6.setProgress(r5)
                                            L_0x00df:
                                                goto L_0x01ea
                                            L_0x00e1:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r4 = r4.progressView
                                                if (r4 == 0) goto L_0x00f6
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$ProgressView r4 = r4.progressView
                                                r4.setProgress(r5)
                                            L_0x00f6:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.destroyTimer()
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.currentType
                                                if (r4 != r6) goto L_0x012a
                                                im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForCall(r9)
                                                im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                                int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveCall
                                                r4.removeObserver(r13, r5)
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                boolean unused = r4.waitingForEvent = r9
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.destroyCodeTimer()
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.resendCode()
                                                goto L_0x01ea
                                            L_0x012a:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.currentType
                                                if (r4 == r10) goto L_0x013e
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.currentType
                                                if (r4 != r8) goto L_0x01ea
                                            L_0x013e:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.nextType
                                                if (r4 == r8) goto L_0x0180
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.nextType
                                                if (r4 != r10) goto L_0x0153
                                                goto L_0x0180
                                            L_0x0153:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.nextType
                                                if (r4 != r6) goto L_0x017f
                                                im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForSms(r9)
                                                im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                                int r5 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveSmsCode
                                                r4.removeObserver(r13, r5)
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                boolean unused = r4.waitingForEvent = r9
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.destroyCodeTimer()
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.resendCode()
                                                goto L_0x01ea
                                            L_0x017f:
                                                goto L_0x01ea
                                            L_0x0180:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                int r4 = r4.nextType
                                                if (r4 != r8) goto L_0x019f
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                android.widget.TextView r4 = r4.timeText
                                                r5 = 2131690302(0x7f0f033e, float:1.9009644E38)
                                                java.lang.String r6 = "Calling"
                                                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                                r4.setText(r5)
                                                goto L_0x01b3
                                            L_0x019f:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                android.widget.TextView r4 = r4.timeText
                                                r5 = 2131693842(0x7f0f1112, float:1.9016824E38)
                                                java.lang.String r6 = "SendingSms"
                                                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                                                r4.setText(r5)
                                            L_0x01b3:
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r4 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                r4.createCodeTimer()
                                                im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode
                                                r4.<init>()
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                java.lang.String r5 = r5.requestPhone
                                                r4.phone_number = r5
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                java.lang.String r5 = r5.phoneHash
                                                r4.phone_code_hash = r5
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5 r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView r5 = im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.this
                                                im.bclpbkiauv.ui.ChangePhoneActivity r5 = r5.this$0
                                                int r5 = r5.currentAccount
                                                im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
                                                im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ r6 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$6grMWMf8FUJIkGumcJJsSMvJ_PQ
                                                r6.<init>(r13)
                                                r5.sendRequest(r4, r6, r10)
                                                goto L_0x017f
                                            L_0x01ea:
                                                return
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.AnonymousClass1.run():void");
                                        }

                                        public /* synthetic */ void lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$5$1(TLObject response, TLRPC.TL_error error) {
                                            if (error != null && error.text != null) {
                                                AndroidUtilities.runOnUIThread(
                                                /*  JADX ERROR: Method code generation error
                                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                                      (wrap: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg : 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg) = 
                                                      (r1v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1 A[THIS])
                                                      (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                                     call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR)
                                                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.1.lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg) = 
                                                      (r1v0 'this' im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1 A[THIS])
                                                      (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                                     call: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg.<init>(im.bclpbkiauv.ui.ChangePhoneActivity$LoginActivitySmsView$5$1, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.5.1.lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                    	... 129 more
                                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg, state: NOT_LOADED
                                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                    	... 135 more
                                                    */
                                                /*
                                                    this = this;
                                                    if (r3 == 0) goto L_0x000e
                                                    java.lang.String r0 = r3.text
                                                    if (r0 == 0) goto L_0x000e
                                                    im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg r0 = new im.bclpbkiauv.ui.-$$Lambda$ChangePhoneActivity$LoginActivitySmsView$5$1$ItmQ4xP8trnM5Tl8LZej61K4Agg
                                                    r0.<init>(r1, r3)
                                                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                                L_0x000e:
                                                    return
                                                */
                                                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChangePhoneActivity.LoginActivitySmsView.AnonymousClass5.AnonymousClass1.lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                                            }

                                            public /* synthetic */ void lambda$null$0$ChangePhoneActivity$LoginActivitySmsView$5$1(TLRPC.TL_error error) {
                                                String unused = LoginActivitySmsView.this.lastError = error.text;
                                            }
                                        });
                                    }
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
                        if (!this.nextPressed) {
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
                            TLRPC.TL_account_changePhone req = new TLRPC.TL_account_changePhone();
                            req.phone_number = this.requestHasBlankPhone;
                            req.phone_code = code;
                            req.phone_code_hash = this.phoneHash;
                            destroyTimer();
                            this.this$0.needShowProgress();
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(req) {
                                private final /* synthetic */ TLRPC.TL_account_changePhone f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    ChangePhoneActivity.LoginActivitySmsView.this.lambda$onNextPressed$7$ChangePhoneActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
                                }
                            }, 2);
                        }
                    }

                    public /* synthetic */ void lambda$onNextPressed$7$ChangePhoneActivity$LoginActivitySmsView(TLRPC.TL_account_changePhone req, TLObject response, TLRPC.TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
                            private final /* synthetic */ TLRPC.TL_error f$1;
                            private final /* synthetic */ TLObject f$2;
                            private final /* synthetic */ TLRPC.TL_account_changePhone f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run() {
                                ChangePhoneActivity.LoginActivitySmsView.this.lambda$null$6$ChangePhoneActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
                            }
                        });
                    }

                    public /* synthetic */ void lambda$null$6$ChangePhoneActivity$LoginActivitySmsView(TLRPC.TL_error error, TLObject response, TLRPC.TL_account_changePhone req) {
                        int i;
                        int i2;
                        this.this$0.needHideProgress();
                        this.nextPressed = false;
                        if (error == null) {
                            TLRPC.User user = (TLRPC.User) response;
                            destroyTimer();
                            destroyCodeTimer();
                            UserConfig.getInstance(this.this$0.currentAccount).setCurrentUser(user);
                            UserConfig.getInstance(this.this$0.currentAccount).saveConfig(true);
                            ArrayList<TLRPC.User> users = new ArrayList<>();
                            users.add(user);
                            MessagesStorage.getInstance(this.this$0.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
                            MessagesController.getInstance(this.this$0.currentAccount).putUser(user, false);
                            this.this$0.finishFragment();
                            NotificationCenter.getInstance(this.this$0.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                            return;
                        }
                        this.lastError = error.text;
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
                            AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
                        }
                        if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                            int a = 0;
                            while (true) {
                                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                                if (a < editTextBoldCursorArr.length) {
                                    editTextBoldCursorArr[a].setText("");
                                    a++;
                                } else {
                                    editTextBoldCursorArr[0].requestFocus();
                                    return;
                                }
                            }
                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                        }
                    }

                    public boolean onBackPressed(boolean force) {
                        if (!force) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ChangePhoneActivity.LoginActivitySmsView.this.lambda$onBackPressed$8$ChangePhoneActivity$LoginActivitySmsView(dialogInterface, i);
                                }
                            });
                            this.this$0.showDialog(builder.create());
                            return false;
                        }
                        TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
                        req.phone_number = this.requestPhone;
                        req.phone_code_hash = this.phoneHash;
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, $$Lambda$ChangePhoneActivity$LoginActivitySmsView$AOP4AvsjsT76P0Pjefr5jq5ctg4.INSTANCE, 10);
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

                    public /* synthetic */ void lambda$onBackPressed$8$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
                        onBackPressed(true);
                        this.this$0.setPage(0, true, (Bundle) null, true);
                    }

                    static /* synthetic */ void lambda$onBackPressed$9(TLObject response, TLRPC.TL_error error) {
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
                                    ChangePhoneActivity.LoginActivitySmsView.this.lambda$onShow$10$ChangePhoneActivity$LoginActivitySmsView();
                                }
                            }, 100);
                        }
                    }

                    public /* synthetic */ void lambda$onShow$10$ChangePhoneActivity$LoginActivitySmsView() {
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
                                    this.ignoreOnTextChange = true;
                                    this.codeField[0].setText(num);
                                    this.ignoreOnTextChange = false;
                                    onNextPressed();
                                }
                            }
                        }
                    }
                }

                public ThemeDescription[] getThemeDescriptions() {
                    SlideView[] slideViewArr = this.views;
                    PhoneView phoneView = (PhoneView) slideViewArr[0];
                    LoginActivitySmsView smsView1 = (LoginActivitySmsView) slideViewArr[1];
                    LoginActivitySmsView smsView2 = (LoginActivitySmsView) slideViewArr[2];
                    LoginActivitySmsView smsView3 = (LoginActivitySmsView) slideViewArr[3];
                    LoginActivitySmsView smsView4 = (LoginActivitySmsView) slideViewArr[4];
                    ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                    ThemeDescription themeDescription = r9;
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
                    arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                    arrayList.add(new ThemeDescription(smsView1.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    if (smsView1.codeField != null) {
                        for (int a = 0; a < smsView1.codeField.length; a++) {
                            arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
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
                        for (int a2 = 0; a2 < smsView2.codeField.length; a2++) {
                            arrayList.add(new ThemeDescription(smsView2.codeField[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(smsView2.codeField[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
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
                        for (int a3 = 0; a3 < smsView3.codeField.length; a3++) {
                            arrayList.add(new ThemeDescription(smsView3.codeField[a3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(smsView3.codeField[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
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
                        for (int a4 = 0; a4 < smsView4.codeField.length; a4++) {
                            arrayList.add(new ThemeDescription(smsView4.codeField[a4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(smsView4.codeField[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                        }
                    }
                    arrayList.add(new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6));
                    arrayList.add(new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                    arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressInner));
                    arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_login_progressOuter));
                    arrayList.add(new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    arrayList.add(new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground));
                    return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
                }
            }
