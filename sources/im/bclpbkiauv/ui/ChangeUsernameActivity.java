package im.bclpbkiauv.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.filter.MaxByteLengthFilter;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryEditText;
import java.util.ArrayList;

public class ChangeUsernameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public TextView checkTextView;
    /* access modifiers changed from: private */
    public View doneButton;
    /* access modifiers changed from: private */
    public MryEditText etAppCode;
    private TextView helpTextView;
    /* access modifiers changed from: private */
    public boolean ignoreCheck;
    /* access modifiers changed from: private */
    public ImageView ivClear;
    private String lastCheckName;
    private boolean lastNameAvailable;

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            this.url = value;
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                ToastUtils.show((int) R.string.LinkCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.ChangeAppNameCode));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (id == 1) {
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString(R.string.Done));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ChangeUsernameActivity$rYLBVVwRfz98UA1EAAT9CqCh06M.INSTANCE);
        FrameLayout nameContainer = new FrameLayout(context2);
        nameContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        nameContainer.setPadding(AndroidUtilities.dp(15.0f), 0, AndroidUtilities.dp(15.0f), 0);
        linearLayout.addView(nameContainer, LayoutHelper.createLinear(-1, 55, 10.0f, 10.0f, 10.0f, 10.0f));
        MryEditText mryEditText = new MryEditText(context2);
        this.etAppCode = mryEditText;
        mryEditText.setTextSize(16.0f);
        this.etAppCode.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.etAppCode.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.etAppCode.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.etAppCode.setMaxLines(1);
        this.etAppCode.setLines(1);
        this.etAppCode.setSingleLine(true);
        this.etAppCode.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.etAppCode.setInputType(180224);
        this.etAppCode.setImeOptions(6);
        this.etAppCode.setHint(LocaleController.getString(R.string.EmptyAppNameCodeTips));
        this.etAppCode.setFilters(new InputFilter[]{new MaxByteLengthFilter()});
        this.etAppCode.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ChangeUsernameActivity.this.ignoreCheck && ChangeUsernameActivity.this.checkTextView != null) {
                    ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                    boolean unused = changeUsernameActivity.checkUserName(changeUsernameActivity.etAppCode.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (editable == null || editable.toString().trim().length() == 0) {
                    if (ChangeUsernameActivity.this.doneButton != null) {
                        ChangeUsernameActivity.this.doneButton.setEnabled(false);
                        ChangeUsernameActivity.this.doneButton.setAlpha(0.5f);
                    }
                    if (ChangeUsernameActivity.this.ivClear != null) {
                        ChangeUsernameActivity.this.ivClear.setVisibility(8);
                        return;
                    }
                    return;
                }
                if (ChangeUsernameActivity.this.doneButton != null) {
                    ChangeUsernameActivity.this.doneButton.setEnabled(true);
                    ChangeUsernameActivity.this.doneButton.setAlpha(1.0f);
                }
                if (ChangeUsernameActivity.this.ivClear != null) {
                    ChangeUsernameActivity.this.ivClear.setVisibility(0);
                }
            }
        });
        this.etAppCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChangeUsernameActivity.this.lambda$createView$1$ChangeUsernameActivity(textView, i, keyEvent);
            }
        });
        this.etAppCode.setFilters(new InputFilter[]{$$Lambda$ChangeUsernameActivity$JLW7ifyPqeqzI3FcA7_c3pJU_o.INSTANCE});
        nameContainer.addView(this.etAppCode, LayoutHelper.createFrame(-1, -1, 0, 0, AndroidUtilities.dp(20.0f), 0));
        ImageView imageView = new ImageView(context2);
        this.ivClear = imageView;
        imageView.setImageResource(R.mipmap.ic_clear_remarks);
        nameContainer.addView(this.ivClear, LayoutHelper.createFrame(-2, -2, 21));
        this.ivClear.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChangeUsernameActivity.this.lambda$createView$3$ChangeUsernameActivity(view);
            }
        });
        TextView textView = new TextView(context2);
        this.checkTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TextView textView2 = new TextView(context2);
        this.helpTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView3 = this.helpTextView;
        textView3.setText(TextUtils.concat(new CharSequence[]{LocaleController.getString("AppCodeHelp1", R.string.AppCodeHelp1) + "\n\n" + LocaleController.getString("AppCodeHelp2", R.string.AppCodeHelp2)}));
        this.helpTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.helpTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        this.helpTextView.setMovementMethod(new LinkMovementMethodMy());
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.checkTextView.setVisibility(8);
        if (!(user == null || user.username == null || user.username.length() <= 0)) {
            this.ignoreCheck = true;
            this.etAppCode.setText(user.username);
            MryEditText mryEditText2 = this.etAppCode;
            mryEditText2.setSelection(mryEditText2.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$ChangeUsernameActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    static /* synthetic */ CharSequence lambda$createView$2(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source == null || !" ".equals(source.toString())) {
            return null;
        }
        return "";
    }

    public /* synthetic */ void lambda$createView$3$ChangeUsernameActivity(View v) {
        MryEditText mryEditText = this.etAppCode;
        if (mryEditText != null) {
            mryEditText.setText((CharSequence) null);
        }
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.etAppCode.requestFocus();
            AndroidUtilities.showKeyboard(this.etAppCode);
        }
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String name, boolean alert) {
        String str = name;
        if (str == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    }
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (str == null || name.length() == 0 || name.length() < 5) {
            if (str == null || name.length() == 0) {
                if (alert) {
                    WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("NoAppNameCodePleaseReEnter", R.string.NoAppNameCodePleaseReEnter), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
                } else {
                    this.checkTextView.setText(LocaleController.getString("NoAppNameCodePleaseReEnter", R.string.NoAppNameCodePleaseReEnter));
                }
            } else if (alert) {
                WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
            }
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else if (name.length() > 24) {
            if (alert) {
                WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            }
            return false;
        } else {
            if (!alert) {
                String currentName = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                if (currentName == null) {
                    currentName = "";
                }
                if (name.equals(currentName)) {
                    this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, str));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                    return true;
                }
                this.checkTextView.setVisibility(8);
                this.lastCheckName = str;
                $$Lambda$ChangeUsernameActivity$gTCHs4Cex41sHGpSOqOyNKlgEkM r0 = new Runnable(name) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChangeUsernameActivity.this.lambda$checkUserName$6$ChangeUsernameActivity(this.f$1);
                    }
                };
                this.checkRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 300);
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$6$ChangeUsernameActivity(String name) {
        TLRPC.TL_account_checkUsername req = new TLRPC.TL_account_checkUsername();
        req.username = name;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(name) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeUsernameActivity.this.lambda$null$5$ChangeUsernameActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$5$ChangeUsernameActivity(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(name, error, response) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChangeUsernameActivity.this.lambda$null$4$ChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$ChangeUsernameActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.checkTextView.setVisibility(0);
                this.lastNameAvailable = false;
                return;
            }
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    public void saveName() {
        String ret = this.etAppCode.getText().toString();
        if (TextUtils.isEmpty(ret)) {
            WalletDialogUtil.showSingleBtnWalletDialog(this, (String) null, LocaleController.getString("NoAppNameCodePleaseReEnter", R.string.NoAppNameCodePleaseReEnter), LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
        } else if (checkUserName(ret, true)) {
            this.checkTextView.setVisibility(8);
            TLRPC.User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (getParentActivity() != null && user != null) {
                String currentName = user.username;
                if (currentName == null) {
                    currentName = "";
                }
                String newName = this.etAppCode.getText().toString();
                if (currentName.equals(newName)) {
                    finishFragment();
                    return;
                }
                XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
                progressDialog.setLoadingText(LocaleController.getString(R.string.SettingUp));
                TLRPC.TL_account_updateUsername req = new TLRPC.TL_account_updateUsername();
                req.username = newName;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, 1);
                int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
                    private final /* synthetic */ XAlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChangeUsernameActivity.this.lambda$saveName$10$ChangeUsernameActivity(this.f$1, tLObject, tL_error);
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ChangeUsernameActivity.this.lambda$saveName$11$ChangeUsernameActivity(this.f$1, dialogInterface);
                    }
                });
                progressDialog.show();
            }
        }
    }

    public /* synthetic */ void lambda$saveName$10$ChangeUsernameActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, (TLRPC.User) response) {
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChangeUsernameActivity.this.lambda$null$8$ChangeUsernameActivity(this.f$1, this.f$2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error) {
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ TLRPC.TL_error f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChangeUsernameActivity.this.lambda$null$9$ChangeUsernameActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$8$ChangeUsernameActivity(XAlertDialog progressDialog, TLRPC.User user1) {
        try {
            progressDialog.setLoadingImage(getParentActivity().getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
            progressDialog.setLoadingText(LocaleController.getString(R.string.SetupSuccess));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    XAlertDialog.this.dismiss();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(user1);
        MessagesController.getInstance(this.currentAccount).putUsers(users, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$9$ChangeUsernameActivity(XAlertDialog progressDialog, TLRPC.TL_error error) {
        String msg;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if ("ALREDY_CHANGE".equals(error.text)) {
            msg = LocaleController.getString("AlreadyChangeAppNameCodeTips", R.string.AlreadyChangeAppNameCodeTips);
        } else {
            msg = LocaleController.getString(R.string.OperationFailedPleaseTryAgain);
        }
        WalletDialogUtil.showSingleBtnWalletDialog(this, LocaleController.getString(R.string.SetupAppNameCodeFail), msg, LocaleController.getString(R.string.OK), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$saveName$11$ChangeUsernameActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.etAppCode.requestFocus();
            AndroidUtilities.showKeyboard(this.etAppCode);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8)};
    }
}
