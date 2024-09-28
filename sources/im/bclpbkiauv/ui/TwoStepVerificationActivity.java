package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SRPHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.EditTextSettingsCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.math.BigInteger;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    private boolean closeAfterSet;
    /* access modifiers changed from: private */
    public EditTextSettingsCell codeFieldCell;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    private String email;
    /* access modifiers changed from: private */
    public int emailCodeLength = 6;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int passwordCodeFieldRow;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private int passwordSetState;
    /* access modifiers changed from: private */
    public int passwordSetupDetailRow;
    private boolean paused;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public int resendCodeRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScrollView scrollView;
    /* access modifiers changed from: private */
    public int setPasswordDetailRow;
    /* access modifiers changed from: private */
    public int setPasswordRow;
    /* access modifiers changed from: private */
    public int setRecoveryEmailRow;
    /* access modifiers changed from: private */
    public int shadowRow;
    private Runnable shortPollRunnable;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int turnPasswordOffRow;
    private int type;
    private boolean waitingForEmail;

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    public TwoStepVerificationActivity(int type2) {
        this.type = type2;
        if (type2 == 0) {
            loadPasswordInfo(false);
        }
    }

    public TwoStepVerificationActivity(int account, int type2) {
        this.currentAccount = account;
        this.type = type2;
        if (type2 == 0) {
            loadPasswordInfo(false);
        }
    }

    /* access modifiers changed from: protected */
    public void setRecoveryParams(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        this.passwordSetState = 4;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
            Runnable runnable = this.shortPollRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.shortPollRunnable = null;
            }
            this.destroyed = true;
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
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TwoStepVerificationActivity.this.finishFragment();
                } else if (id == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return TwoStepVerificationActivity.this.lambda$createView$0$TwoStepVerificationActivity(textView, i, keyEvent);
            }
        });
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        TextView textView2 = new TextView(context2);
        this.bottomTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        TextView textView3 = new TextView(context2);
        this.bottomButton = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 80, 40, 0, 40, 14));
        this.bottomButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivity.this.lambda$createView$6$TwoStepVerificationActivity(view);
            }
        });
        int i = this.type;
        if (i == 0) {
            EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
            this.emptyView = emptyTextProgressView;
            emptyTextProgressView.showProgress();
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = new RecyclerListView(context2);
            this.listView = recyclerListView;
            recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            this.listView.setEmptyView(this.emptyView);
            this.listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView2 = this.listView;
            ListAdapter listAdapter2 = new ListAdapter(context2);
            this.listAdapter = listAdapter2;
            recyclerListView2.setAdapter(listAdapter2);
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    TwoStepVerificationActivity.this.lambda$createView$9$TwoStepVerificationActivity(view, i);
                }
            });
            EditTextSettingsCell editTextSettingsCell = new EditTextSettingsCell(context2);
            this.codeFieldCell = editTextSettingsCell;
            editTextSettingsCell.setTextAndHint("", LocaleController.getString("PasswordCode", R.string.PasswordCode), false);
            this.codeFieldCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            EditTextBoldCursor editText = this.codeFieldCell.getTextView();
            editText.setInputType(3);
            editText.setImeOptions(6);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return TwoStepVerificationActivity.this.lambda$createView$10$TwoStepVerificationActivity(textView, i, keyEvent);
                }
            });
            editText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (TwoStepVerificationActivity.this.emailCodeLength != 0 && s.length() == TwoStepVerificationActivity.this.emailCodeLength) {
                        TwoStepVerificationActivity.this.processDone();
                    }
                }
            });
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle));
            if (this.delegate != null) {
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPasswordTransfer", R.string.PleaseEnterCurrentPasswordTransfer));
            } else {
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", R.string.PleaseEnterCurrentPassword));
            }
        } else if (i == 1) {
            setPasswordSetState(this.passwordSetState);
        }
        if (!this.passwordEntered || this.type == 1) {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$0$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public /* synthetic */ void lambda$createView$6$TwoStepVerificationActivity(View v) {
        if (this.type == 0) {
            if (this.currentPassword.has_recovery) {
                needShowProgress();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationActivity.this.lambda$null$3$TwoStepVerificationActivity(tLObject, tL_error);
                    }
                }, 10);
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$4$TwoStepVerificationActivity(dialogInterface, i);
                    }
                });
                builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                showDialog(builder.create());
            }
        } else if (this.passwordSetState == 4) {
            showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setMessage(LocaleController.getString("YourEmailSkipWarningText", R.string.YourEmailSkipWarningText));
            builder2.setTitle(LocaleController.getString("YourEmailSkipWarning", R.string.YourEmailSkipWarning));
            builder2.setPositiveButton(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$5$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
        }
    }

    public /* synthetic */ void lambda$null$3$TwoStepVerificationActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$2$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$TwoStepVerificationActivity(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(res) {
                private final /* synthetic */ TLRPC.TL_auth_passwordRecovery f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$1$TwoStepVerificationActivity(this.f$1, dialogInterface, i);
                }
            });
            Dialog dialog = showDialog(builder.create());
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
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    public /* synthetic */ void lambda$null$1$TwoStepVerificationActivity(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
        TLRPC.TL_account_password tL_account_password = this.currentPassword;
        fragment.currentPassword = tL_account_password;
        tL_account_password.email_unconfirmed_pattern = res.email_pattern;
        fragment.currentSecretId = this.currentSecretId;
        fragment.currentSecret = this.currentSecret;
        fragment.passwordSetState = 4;
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$null$4$TwoStepVerificationActivity(DialogInterface dialog, int which) {
        FragmentActivity parentActivity = getParentActivity();
        Browser.openUrl((Context) parentActivity, "https://m12345.com/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    public /* synthetic */ void lambda$null$5$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    public /* synthetic */ void lambda$createView$9$TwoStepVerificationActivity(View view, int position) {
        String text;
        if (position == this.setPasswordRow || position == this.changePasswordRow) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
            fragment.currentPasswordHash = this.currentPasswordHash;
            fragment.currentPassword = this.currentPassword;
            fragment.currentSecretId = this.currentSecretId;
            fragment.currentSecret = this.currentSecret;
            presentFragment(fragment);
        } else if (position == this.setRecoveryEmailRow || position == this.changeRecoveryEmailRow) {
            TwoStepVerificationActivity fragment2 = new TwoStepVerificationActivity(this.currentAccount, 1);
            fragment2.currentPasswordHash = this.currentPasswordHash;
            fragment2.currentPassword = this.currentPassword;
            fragment2.currentSecretId = this.currentSecretId;
            fragment2.currentSecret = this.currentSecret;
            fragment2.emailOnly = true;
            fragment2.passwordSetState = 3;
            presentFragment(fragment2);
        } else if (position == this.turnPasswordOffRow || position == this.abortPasswordRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (position == this.abortPasswordRow) {
                TLRPC.TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password == null || !tL_account_password.has_password) {
                    text = LocaleController.getString("CancelPasswordQuestion", R.string.CancelPasswordQuestion);
                } else {
                    text = LocaleController.getString("CancelEmailQuestion", R.string.CancelEmailQuestion);
                }
            } else {
                text = LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion);
                if (this.currentPassword.has_secure_values) {
                    text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", R.string.TurnPasswordOffPassport);
                }
            }
            builder.setMessage(text);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$7$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (position == this.resendCodeRow) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), $$Lambda$TwoStepVerificationActivity$QRWEaZqqS_aqBgMdVObNV5WH0DA.INSTANCE);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setMessage(LocaleController.getString("ResendCodeInfo", R.string.ResendCodeInfo));
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
        }
    }

    public /* synthetic */ void lambda$null$7$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        setNewPassword(true);
    }

    static /* synthetic */ void lambda$null$8(TLObject response, TLRPC.TL_error error) {
    }

    public /* synthetic */ boolean lambda$createView$10$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = args[0];
                if (this.closeAfterSet && TextUtils.isEmpty(args[4]) && this.closeAfterSet) {
                    removeSelfFromStack();
                }
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        EditTextSettingsCell editTextSettingsCell;
        super.onResume();
        this.paused = false;
        int i = this.type;
        if (i == 1) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$onResume$11$TwoStepVerificationActivity();
                }
            }, 200);
        } else if (i == 0 && (editTextSettingsCell = this.codeFieldCell) != null && editTextSettingsCell.getVisibility() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$onResume$12$TwoStepVerificationActivity();
                }
            }, 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public /* synthetic */ void lambda$onResume$11$TwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public /* synthetic */ void lambda$onResume$12$TwoStepVerificationActivity() {
        EditTextSettingsCell editTextSettingsCell = this.codeFieldCell;
        if (editTextSettingsCell != null) {
            editTextSettingsCell.getTextView().requestFocus();
            AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
        }
    }

    public void setCloseAfterSet(boolean value) {
        this.closeAfterSet = value;
    }

    public void setCurrentPasswordInfo(byte[] hash, TLRPC.TL_account_password password) {
        if (hash != null) {
            this.currentPasswordHash = hash;
        }
        this.currentPassword = password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        EditTextSettingsCell editTextSettingsCell;
        if (isOpen) {
            int i = this.type;
            if (i == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            } else if (i == 0 && (editTextSettingsCell = this.codeFieldCell) != null && editTextSettingsCell.getVisibility() == 0) {
                AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
            }
        }
    }

    public static boolean canHandleCurrentPassword(TLRPC.TL_account_password password, boolean login) {
        if (login) {
            if (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((password.new_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC.TL_account_password password) {
        if (password.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) password.new_algo;
            byte[] salt = new byte[(algo.salt1.length + 32)];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo.salt1, 0, salt, 0, algo.salt1.length);
            algo.salt1 = salt;
        }
        if (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) password.new_secure_algo;
            byte[] salt2 = new byte[(algo2.salt.length + 32)];
            Utilities.random.nextBytes(salt2);
            System.arraycopy(algo2.salt, 0, salt2, 0, algo2.salt.length);
            algo2.salt = salt2;
        }
    }

    private void loadPasswordInfo(boolean silent) {
        if (!silent) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(silent) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$loadPasswordInfo$14$TwoStepVerificationActivity(this.f$1, tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$loadPasswordInfo$14$TwoStepVerificationActivity(boolean silent, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, silent) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$13$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$TwoStepVerificationActivity(TLRPC.TL_error error, TLObject response, boolean silent) {
        TLRPC.TL_account_password tL_account_password;
        if (error == null) {
            this.loading = false;
            TLRPC.TL_account_password tL_account_password2 = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password2;
            if (!canHandleCurrentPassword(tL_account_password2, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            if (!silent) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet && this.currentPassword.has_password) {
                TLRPC.PasswordKdfAlgo pendingCurrentAlgo = this.currentPassword.current_algo;
                TLRPC.SecurePasswordKdfAlgo pendingNewSecureAlgo = this.currentPassword.new_secure_algo;
                byte[] pendingSecureRandom = this.currentPassword.secure_random;
                String pendingEmail = this.currentPassword.has_recovery ? "1" : null;
                String pendingHint = this.currentPassword.hint != null ? this.currentPassword.hint : "";
                if (!this.waitingForEmail && pendingCurrentAlgo != null) {
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, null, pendingCurrentAlgo, pendingNewSecureAlgo, pendingSecureRandom, pendingEmail, pendingHint, null, null);
                    finishFragment();
                }
            }
        }
        if (this.type == 0 && !this.destroyed && this.shortPollRunnable == null && (tL_account_password = this.currentPassword) != null && !TextUtils.isEmpty(tL_account_password.email_unconfirmed_pattern)) {
            startShortpoll();
        }
        updateRows();
    }

    private void startShortpoll() {
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        $$Lambda$TwoStepVerificationActivity$ehl1g0OzFiKlL5KfUcaAzFCOFrk r0 = new Runnable() {
            public final void run() {
                TwoStepVerificationActivity.this.lambda$startShortpoll$15$TwoStepVerificationActivity();
            }
        };
        this.shortPollRunnable = r0;
        AndroidUtilities.runOnUIThread(r0, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public /* synthetic */ void lambda$startShortpoll$15$TwoStepVerificationActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo(true);
            this.shortPollRunnable = null;
        }
    }

    private void setPasswordSetState(int state) {
        String str;
        if (this.passwordEditText != null) {
            this.passwordSetState = state;
            int i = 4;
            if (state == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", R.string.YourPassword));
                if (this.currentPassword.has_password) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", R.string.PleaseEnterPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", R.string.PleaseEnterFirstPassword));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (state == 1) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", R.string.YourPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", R.string.PleaseReEnterPassword));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (state == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", R.string.PasswordHint));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", R.string.PasswordHintText));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (state == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", R.string.RecoveryEmail));
                this.titleTextView.setText(LocaleController.getString("YourEmail", R.string.YourEmail));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                TextView textView = this.bottomButton;
                if (!this.emailOnly) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (state == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", R.string.RestoreEmailSentInfo));
                TextView textView2 = this.bottomButton;
                Object[] objArr = new Object[1];
                if (this.currentPassword.email_unconfirmed_pattern != null) {
                    str = this.currentPassword.email_unconfirmed_pattern;
                } else {
                    str = "";
                }
                objArr[0] = str;
                textView2.setText(LocaleController.formatString("RestoreEmailTrouble", R.string.RestoreEmailTrouble, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText("");
        }
    }

    private void updateRows() {
        StringBuilder lastValue = new StringBuilder();
        lastValue.append(this.setPasswordRow);
        lastValue.append(this.setPasswordDetailRow);
        lastValue.append(this.changePasswordRow);
        lastValue.append(this.turnPasswordOffRow);
        lastValue.append(this.setRecoveryEmailRow);
        lastValue.append(this.changeRecoveryEmailRow);
        lastValue.append(this.resendCodeRow);
        lastValue.append(this.abortPasswordRow);
        lastValue.append(this.passwordSetupDetailRow);
        lastValue.append(this.passwordCodeFieldRow);
        lastValue.append(this.passwordEnabledDetailRow);
        lastValue.append(this.shadowRow);
        lastValue.append(this.rowCount);
        boolean wasCodeField = this.passwordCodeFieldRow != -1;
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.abortPasswordRow = -1;
        this.resendCodeRow = -1;
        this.passwordSetupDetailRow = -1;
        this.passwordCodeFieldRow = -1;
        this.passwordEnabledDetailRow = -1;
        this.shadowRow = -1;
        if (!this.loading) {
            if (this.waitingForEmail) {
                int i = 0 + 1;
                this.rowCount = i;
                this.passwordCodeFieldRow = 0;
                int i2 = i + 1;
                this.rowCount = i2;
                this.passwordSetupDetailRow = i;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.resendCodeRow = i2;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.abortPasswordRow = i3;
                this.rowCount = i4 + 1;
                this.shadowRow = i4;
            } else {
                TLRPC.TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password == null || !tL_account_password.has_password) {
                    int i5 = this.rowCount;
                    int i6 = i5 + 1;
                    this.rowCount = i6;
                    this.setPasswordRow = i5;
                    this.rowCount = i6 + 1;
                    this.setPasswordDetailRow = i6;
                } else {
                    int i7 = this.rowCount;
                    int i8 = i7 + 1;
                    this.rowCount = i8;
                    this.changePasswordRow = i7;
                    this.rowCount = i8 + 1;
                    this.turnPasswordOffRow = i8;
                    if (this.currentPassword.has_recovery) {
                        int i9 = this.rowCount;
                        this.rowCount = i9 + 1;
                        this.changeRecoveryEmailRow = i9;
                    } else {
                        int i10 = this.rowCount;
                        this.rowCount = i10 + 1;
                        this.setRecoveryEmailRow = i10;
                    }
                    int i11 = this.rowCount;
                    this.rowCount = i11 + 1;
                    this.passwordEnabledDetailRow = i11;
                }
            }
        }
        StringBuilder newValue = new StringBuilder();
        newValue.append(this.setPasswordRow);
        newValue.append(this.setPasswordDetailRow);
        newValue.append(this.changePasswordRow);
        newValue.append(this.turnPasswordOffRow);
        newValue.append(this.setRecoveryEmailRow);
        newValue.append(this.changeRecoveryEmailRow);
        newValue.append(this.resendCodeRow);
        newValue.append(this.abortPasswordRow);
        newValue.append(this.passwordSetupDetailRow);
        newValue.append(this.passwordCodeFieldRow);
        newValue.append(this.passwordEnabledDetailRow);
        newValue.append(this.shadowRow);
        newValue.append(this.rowCount);
        if (this.listAdapter != null && !lastValue.toString().equals(newValue.toString())) {
            this.listAdapter.notifyDataSetChanged();
            if (this.passwordCodeFieldRow == -1 && getParentActivity() != null && wasCodeField) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                this.codeFieldCell.setText("", false);
            }
        }
        if (this.fragmentView == null) {
            return;
        }
        if (this.loading || this.passwordEntered) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.waitingForEmail && this.currentPassword != null) {
                this.doneItem.setVisibility(0);
            } else if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            }
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
            return;
        }
        RecyclerListView recyclerListView2 = this.listView;
        if (recyclerListView2 != null) {
            recyclerListView2.setEmptyView((View) null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.emptyView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            this.passwordEditText.setHint("");
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$updateRows$16$TwoStepVerificationActivity();
                }
            }, 200);
        }
    }

    public /* synthetic */ void lambda$updateRows$16$TwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void showDoneProgress(boolean show) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        if (z) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animation)) {
                    if (!z) {
                        TwoStepVerificationActivity.this.progressView.setVisibility(4);
                    } else {
                        TwoStepVerificationActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animation)) {
                    AnimatorSet unused = TwoStepVerificationActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.progressDialog.show();
        }
    }

    /* access modifiers changed from: protected */
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

    private boolean isValidEmail(String text) {
        if (text == null || text.length() < 3) {
            return false;
        }
        int dot = text.lastIndexOf(46);
        int dog = text.lastIndexOf(64);
        if (dog < 0 || dot < dog) {
            return false;
        }
        return true;
    }

    private void showAlertWithText(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void setNewPassword(boolean clear) {
        TLRPC.TL_account_password tL_account_password;
        if (!clear || !this.waitingForEmail || !this.currentPassword.has_password) {
            String password = this.firstPassword;
            TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
            byte[] bArr = this.currentPasswordHash;
            if (bArr == null || bArr.length == 0) {
                req.password = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            req.new_settings = new TLRPC.TL_account_passwordInputSettings();
            if (clear) {
                UserConfig.getInstance(this.currentAccount).resetSavedPassword();
                this.currentSecret = null;
                if (this.waitingForEmail) {
                    req.new_settings.flags = 2;
                    req.new_settings.email = "";
                    req.password = new TLRPC.TL_inputCheckPasswordEmpty();
                } else {
                    req.new_settings.flags = 3;
                    req.new_settings.hint = "";
                    req.new_settings.new_password_hash = new byte[0];
                    req.new_settings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
                    req.new_settings.email = "";
                }
            } else {
                if (this.hint == null && (tL_account_password = this.currentPassword) != null) {
                    this.hint = tL_account_password.hint;
                }
                if (this.hint == null) {
                    this.hint = "";
                }
                if (password != null) {
                    req.new_settings.flags |= 1;
                    req.new_settings.hint = this.hint;
                    req.new_settings.new_algo = this.currentPassword.new_algo;
                }
                if (this.email.length() > 0) {
                    TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                    tL_account_passwordInputSettings.flags = 2 | tL_account_passwordInputSettings.flags;
                    req.new_settings.email = this.email.trim();
                }
            }
            needShowProgress();
            Utilities.globalQueue.postRunnable(new Runnable(req, clear, password) {
                private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$setNewPassword$25$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_cancelPasswordEmail(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$setNewPassword$18$TwoStepVerificationActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setNewPassword$18$TwoStepVerificationActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$17$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$TwoStepVerificationActivity(TLRPC.TL_error error) {
        needHideProgress();
        if (error == null) {
            loadPasswordInfo(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            updateRows();
        }
    }

    public /* synthetic */ void lambda$setNewPassword$25$TwoStepVerificationActivity(TLRPC.TL_account_updatePasswordSettings req, boolean clear, String password) {
        byte[] newPasswordHash;
        byte[] newPasswordBytes;
        byte[] bArr;
        TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = req;
        if (tL_account_updatePasswordSettings.password == null) {
            tL_account_updatePasswordSettings.password = getNewSrpPassword();
        }
        if (clear || password == null) {
            newPasswordBytes = null;
            newPasswordHash = null;
        } else {
            byte[] newPasswordBytes2 = AndroidUtilities.getStringBytes(password);
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = SRPHelper.getX(newPasswordBytes2, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
            } else {
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = null;
            }
        }
        $$Lambda$TwoStepVerificationActivity$W1PSRLrJFQOzukH0m0P6KvtaFYo r0 = new RequestDelegate(clear, newPasswordHash, req, password) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ byte[] f$2;
            private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$null$24$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
            }
        };
        if (!clear) {
            if (password != null && (bArr = this.currentSecret) != null && bArr.length == 32 && (this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000)) {
                TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) this.currentPassword.new_secure_algo;
                byte[] passwordHash = Utilities.computePBKDF2(newPasswordBytes, newAlgo.salt);
                byte[] key = new byte[32];
                System.arraycopy(passwordHash, 0, key, 0, 32);
                byte[] iv = new byte[16];
                System.arraycopy(passwordHash, 32, iv, 0, 16);
                byte[] encryptedSecret = new byte[32];
                System.arraycopy(this.currentSecret, 0, encryptedSecret, 0, 32);
                byte[] bArr2 = iv;
                Utilities.aesCbcEncryptionByteArraySafe(encryptedSecret, key, iv, 0, encryptedSecret.length, 0, 1);
                tL_account_updatePasswordSettings.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                tL_account_updatePasswordSettings.new_settings.new_secure_settings.secure_algo = newAlgo;
                tL_account_updatePasswordSettings.new_settings.new_secure_settings.secure_secret = encryptedSecret;
                tL_account_updatePasswordSettings.new_settings.new_secure_settings.secure_secret_id = this.currentSecretId;
                tL_account_updatePasswordSettings.new_settings.flags |= 4;
            }
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (password != null) {
                    tL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
                    if (tL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "ALGO_INVALID";
                        r0.run((TLObject) null, error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, r0, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            r0.run((TLObject) null, error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, r0, 10);
    }

    public /* synthetic */ void lambda$null$24$TwoStepVerificationActivity(boolean clear, byte[] newPasswordHash, TLRPC.TL_account_updatePasswordSettings req, String password, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, clear, response, newPasswordHash, req, password) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ byte[] f$4;
            private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$5;
            private final /* synthetic */ String f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$23$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$23$TwoStepVerificationActivity(TLRPC.TL_error error, boolean clear, TLObject response, byte[] newPasswordHash, TLRPC.TL_account_updatePasswordSettings req, String password) {
        String timeString;
        TLRPC.TL_account_password tL_account_password;
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            needHideProgress();
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null) {
                    return;
                }
                if ("EMAIL_UNCONFIRMED".equals(error.text) || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(newPasswordHash, req) {
                        private final /* synthetic */ byte[] f$1;
                        private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationActivity.this.lambda$null$22$TwoStepVerificationActivity(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                    }
                } else if ("EMAIL_INVALID".equals(error.text)) {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                }
            } else if (clear) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                loadPasswordInfo(false);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                updateRows();
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(newPasswordHash, req) {
                    private final /* synthetic */ byte[] f$1;
                    private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$21$TwoStepVerificationActivity(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                if (password != null || (tL_account_password = this.currentPassword) == null || !tL_account_password.has_password) {
                    builder2.setMessage(LocaleController.getString("YourPasswordSuccessText", R.string.YourPasswordSuccessText));
                } else {
                    builder2.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
                }
                builder2.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
                Dialog dialog2 = showDialog(builder2.create());
                if (dialog2 != null) {
                    dialog2.setCanceledOnTouchOutside(false);
                    dialog2.setCancelable(false);
                }
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(clear) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.lambda$null$20$TwoStepVerificationActivity(this.f$1, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$null$20$TwoStepVerificationActivity(boolean clear, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable(error2, response2, clear) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$19$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$TwoStepVerificationActivity(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            setNewPassword(clear);
        }
    }

    public /* synthetic */ void lambda$null$21$TwoStepVerificationActivity(byte[] newPasswordHash, TLRPC.TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$22$TwoStepVerificationActivity(byte[] newPasswordHash, TLRPC.TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        if (this.closeAfterSet) {
            TwoStepVerificationActivity activity = new TwoStepVerificationActivity(this.currentAccount, 0);
            activity.setCloseAfterSet(true);
            this.parentLayout.addFragmentToStack(activity, this.parentLayout.fragmentsStack.size() - 1);
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        String str = this.email;
        instance.postNotificationName(i2, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, str, this.hint, str, this.firstPassword);
        finishFragment();
    }

    /* access modifiers changed from: protected */
    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
    }

    private boolean checkSecretValues(byte[] passwordBytes, TLRPC.TL_account_passwordSettings passwordSettings) {
        byte[] passwordHash;
        byte[] bArr = passwordBytes;
        TLRPC.TL_account_passwordSettings tL_account_passwordSettings = passwordSettings;
        if (tL_account_passwordSettings.secure_settings != null) {
            this.currentSecret = tL_account_passwordSettings.secure_settings.secure_secret;
            if (tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                passwordHash = Utilities.computePBKDF2(bArr, ((TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tL_account_passwordSettings.secure_settings.secure_algo).salt);
            } else if (!(tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                TLRPC.TL_securePasswordKdfAlgoSHA512 algo = (TLRPC.TL_securePasswordKdfAlgoSHA512) tL_account_passwordSettings.secure_settings.secure_algo;
                passwordHash = Utilities.computeSHA512(algo.salt, bArr, algo.salt);
            }
            this.currentSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            byte[] bArr2 = this.currentSecret;
            byte[] bArr3 = iv;
            byte[] bArr4 = key;
            Utilities.aesCbcEncryptionByteArraySafe(bArr2, key, iv, 0, bArr2.length, 0, 0);
            if (PassportActivity.checkSecret(tL_account_passwordSettings.secure_settings.secure_secret, Long.valueOf(tL_account_passwordSettings.secure_settings.secure_secret_id))) {
                return true;
            }
            TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
            req.password = getNewSrpPassword();
            req.new_settings = new TLRPC.TL_account_passwordInputSettings();
            req.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            req.new_settings.new_secure_settings.secure_secret = new byte[0];
            req.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            req.new_settings.new_secure_settings.secure_secret_id = 0;
            req.new_settings.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$TwoStepVerificationActivity$tr_R4xM9u8dhCGl2Vyxr0s9Bze8.INSTANCE);
            this.currentSecret = null;
            this.currentSecretId = 0;
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0;
        return true;
    }

    static /* synthetic */ void lambda$checkSecretValues$26(TLObject response, TLRPC.TL_error error) {
    }

    private static byte[] getBigIntegerBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length <= 256) {
            return bytes;
        }
        byte[] correctedAuth = new byte[256];
        System.arraycopy(bytes, 1, correctedAuth, 0, 256);
        return correctedAuth;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        int i = this.type;
        if (i == 0) {
            if (!this.passwordEntered) {
                String oldPassword = this.passwordEditText.getText().toString();
                if (oldPassword.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new Runnable(oldPasswordBytes) {
                    private final /* synthetic */ byte[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TwoStepVerificationActivity.this.lambda$processDone$33$TwoStepVerificationActivity(this.f$1);
                    }
                });
            } else if (this.waitingForEmail && this.currentPassword != null) {
                if (this.codeFieldCell.length() == 0) {
                    onFieldError(this.codeFieldCell.getTextView(), false);
                    return;
                }
                sendEmailConfirm(this.codeFieldCell.getText());
                showDoneProgress(true);
            }
        } else if (i == 1) {
            int i2 = this.passwordSetState;
            if (i2 == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (i2 == 1) {
                if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    ToastUtils.show((int) R.string.PasswordDoNotMatch);
                    onFieldError(this.passwordEditText, true);
                    return;
                }
                setPasswordSetState(2);
            } else if (i2 == 2) {
                String obj = this.passwordEditText.getText().toString();
                this.hint = obj;
                if (obj.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    ToastUtils.show((int) R.string.PasswordAsHintError);
                    onFieldError(this.passwordEditText, false);
                } else if (!this.currentPassword.has_recovery) {
                    setPasswordSetState(3);
                } else {
                    this.email = "";
                    setNewPassword(false);
                }
            } else if (i2 == 3) {
                String obj2 = this.passwordEditText.getText().toString();
                this.email = obj2;
                if (!isValidEmail(obj2)) {
                    onFieldError(this.passwordEditText, false);
                } else {
                    setNewPassword(false);
                }
            } else if (i2 == 4) {
                String code = this.passwordEditText.getText().toString();
                if (code.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
                req.code = code;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationActivity.this.lambda$processDone$36$TwoStepVerificationActivity(tLObject, tL_error);
                    }
                }, 10);
            }
        }
    }

    public /* synthetic */ void lambda$processDone$33$TwoStepVerificationActivity(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new RequestDelegate(oldPasswordBytes, x_bytes) {
            private final /* synthetic */ byte[] f$1;
            private final /* synthetic */ byte[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$null$32$TwoStepVerificationActivity(this.f$1, this.f$2, tLObject, tL_error);
            }
        };
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run((TLObject) null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run((TLObject) null, error2);
    }

    public /* synthetic */ void lambda$null$32$TwoStepVerificationActivity(byte[] oldPasswordBytes, byte[] x_bytes, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new Runnable(oldPasswordBytes, response, x_bytes) {
                private final /* synthetic */ byte[] f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ byte[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$null$28$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(error) {
                private final /* synthetic */ TLRPC.TL_error f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$null$31$TwoStepVerificationActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$28$TwoStepVerificationActivity(byte[] oldPasswordBytes, TLObject response, byte[] x_bytes) {
        AndroidUtilities.runOnUIThread(new Runnable(checkSecretValues(oldPasswordBytes, (TLRPC.TL_account_passwordSettings) response), x_bytes) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ byte[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$27$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$27$TwoStepVerificationActivity(boolean secretOk, byte[] x_bytes) {
        if (this.delegate == null || !secretOk) {
            needHideProgress();
        }
        if (secretOk) {
            this.currentPasswordHash = x_bytes;
            this.passwordEntered = true;
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate = this.delegate;
            if (twoStepVerificationActivityDelegate != null) {
                twoStepVerificationActivityDelegate.didEnterPassword(getNewSrpPassword());
            } else {
                updateRows();
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        }
    }

    public /* synthetic */ void lambda$null$31$TwoStepVerificationActivity(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.lambda$null$30$TwoStepVerificationActivity(tLObject, tL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            onFieldError(this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    public /* synthetic */ void lambda$null$30$TwoStepVerificationActivity(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable(error2, response2) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$29$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$29$TwoStepVerificationActivity(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            processDone();
        }
    }

    public /* synthetic */ void lambda$processDone$36$TwoStepVerificationActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$35$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$35$TwoStepVerificationActivity(TLRPC.TL_error error) {
        String timeString;
        if (error == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$34$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder.setMessage(LocaleController.getString("PasswordReset", R.string.PasswordReset));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    public /* synthetic */ void lambda$null$34$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
        finishFragment();
    }

    private void sendEmailConfirm(String code) {
        TLRPC.TL_account_confirmPasswordEmail req = new TLRPC.TL_account_confirmPasswordEmail();
        req.code = code;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$sendEmailConfirm$39$TwoStepVerificationActivity(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$sendEmailConfirm$39$TwoStepVerificationActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$38$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$38$TwoStepVerificationActivity(TLRPC.TL_error error) {
        String timeString;
        if (this.type == 0 && this.waitingForEmail) {
            showDoneProgress(false);
        }
        if (error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$37$TwoStepVerificationActivity(dialogInterface, i);
                    }
                });
                TLRPC.TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password == null || !tL_account_password.has_password) {
                    builder.setMessage(LocaleController.getString("YourPasswordSuccessText", R.string.YourPasswordSuccessText));
                } else {
                    builder.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
                }
                builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
                Dialog dialog = showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.waitingForEmail ? this.codeFieldCell.getTextView() : this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    public /* synthetic */ void lambda$null$37$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        if (this.type == 0) {
            loadPasswordInfo(false);
            this.doneItem.setVisibility(8);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        finishFragment();
    }

    private void onFieldError(TextView field, boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                field.setText("");
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType != 1) {
                view = TwoStepVerificationActivity.this.codeFieldCell;
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                if (position == TwoStepVerificationActivity.this.changePasswordRow) {
                    textCell.setText(LocaleController.getString("ChangePassword", R.string.ChangePassword), true);
                } else if (position == TwoStepVerificationActivity.this.setPasswordRow) {
                    textCell.setText(LocaleController.getString("SetAdditionalPassword", R.string.SetAdditionalPassword), true);
                } else if (position == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textCell.setText(LocaleController.getString("TurnPasswordOff", R.string.TurnPasswordOff), true);
                } else if (position == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    String string = LocaleController.getString("ChangeRecoveryEmail", R.string.ChangeRecoveryEmail);
                    if (TwoStepVerificationActivity.this.abortPasswordRow != -1) {
                        z = true;
                    }
                    textCell.setText(string, z);
                } else if (position == TwoStepVerificationActivity.this.resendCodeRow) {
                    textCell.setText(LocaleController.getString("ResendCode", R.string.ResendCode), true);
                } else if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textCell.setText(LocaleController.getString("SetRecoveryEmail", R.string.SetRecoveryEmail), false);
                } else if (position == TwoStepVerificationActivity.this.abortPasswordRow) {
                    textCell.setTag(Theme.key_windowBackgroundWhiteRedText3);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        textCell.setText(LocaleController.getString("AbortPassword", R.string.AbortPassword), false);
                    } else {
                        textCell.setText(LocaleController.getString("AbortEmail", R.string.AbortEmail), false);
                    }
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                }
                String str = "";
                if (position == TwoStepVerificationActivity.this.shadowRow) {
                    privacyCell.setText(str);
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                } else if (position == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        Object[] objArr = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr[0] = str;
                        privacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", R.string.EmailPasswordConfirmText2, objArr));
                    } else {
                        Object[] objArr2 = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr2[0] = str;
                        privacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText3", R.string.EmailPasswordConfirmText3, objArr2));
                    }
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                } else if (position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    privacyCell.setText(LocaleController.getString("EnabledPasswordText", R.string.EnabledPasswordText));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == TwoStepVerificationActivity.this.setPasswordDetailRow || position == TwoStepVerificationActivity.this.shadowRow || position == TwoStepVerificationActivity.this.passwordSetupDetailRow || position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            if (position == TwoStepVerificationActivity.this.passwordCodeFieldRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText3), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated)};
    }
}
