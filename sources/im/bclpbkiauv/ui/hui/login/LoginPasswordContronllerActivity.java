package im.bclpbkiauv.ui.hui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.translate.MD5;
import im.bclpbkiauv.ui.IndexActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.login.LoginContronllerBaseActivity;
import im.bclpbkiauv.ui.hui.login.LoginPasswordContronllerActivity;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryEditText;
import java.io.PrintStream;

public class LoginPasswordContronllerActivity extends LoginContronllerBaseActivity implements NotificationCenter.NotificationCenterDelegate {
    public static final int PASSWORD_MODOIFY = 1;
    public static final int PASSWORD_RESET = 0;
    public static final int PASSWORD_SET = 2;
    public TextView backupIpAddressLog;
    /* access modifiers changed from: private */
    public int contronllerType;

    public class StepThree_ViewBinding extends LoginContronllerBaseActivity.ThisView_ViewBinding {
        private StepThree target;
        private View view7f0901d2;
        private View view7f0901d3;
        private View view7f0901d4;
        private View view7f0901f6;
        private View view7f0901f7;
        private View view7f0901f8;

        public StepThree_ViewBinding(StepThree target2) {
            this(target2, target2);
        }

        public StepThree_ViewBinding(final StepThree target2, View source) {
            super(target2, source);
            this.target = target2;
            target2.etPwd1Parent = Utils.findRequiredView(source, R.id.etPwd1Parent, "field 'etPwd1Parent'");
            target2.etPwd1 = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPwd1, "field 'etPwd1'", MryEditText.class);
            View view = Utils.findRequiredView(source, R.id.ivClear1, "field 'ivClear1' and method 'onClick'");
            target2.ivClear1 = (MryAlphaImageView) Utils.castView(view, R.id.ivClear1, "field 'ivClear1'", MryAlphaImageView.class);
            this.view7f0901d2 = view;
            view.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view2 = Utils.findRequiredView(source, R.id.ivPwdShow1, "field 'ivPwdShow1' and method 'onClick'");
            target2.ivPwdShow1 = (MryAlphaImageView) Utils.castView(view2, R.id.ivPwdShow1, "field 'ivPwdShow1'", MryAlphaImageView.class);
            this.view7f0901f6 = view2;
            view2.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.etPwd2Parent = Utils.findRequiredView(source, R.id.etPwd2Parent, "field 'etPwd2Parent'");
            target2.etPwd2 = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPwd2, "field 'etPwd2'", MryEditText.class);
            View view3 = Utils.findRequiredView(source, R.id.ivClear2, "field 'ivClear2' and method 'onClick'");
            target2.ivClear2 = (MryAlphaImageView) Utils.castView(view3, R.id.ivClear2, "field 'ivClear2'", MryAlphaImageView.class);
            this.view7f0901d3 = view3;
            view3.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view4 = Utils.findRequiredView(source, R.id.ivPwdShow2, "field 'ivPwdShow2' and method 'onClick'");
            target2.ivPwdShow2 = (MryAlphaImageView) Utils.castView(view4, R.id.ivPwdShow2, "field 'ivPwdShow2'", MryAlphaImageView.class);
            this.view7f0901f7 = view4;
            view4.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.etPwd3Parent = Utils.findRequiredView(source, R.id.etPwd3Parent, "field 'etPwd3Parent'");
            target2.etPwd3 = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPwd3, "field 'etPwd3'", MryEditText.class);
            View view5 = Utils.findRequiredView(source, R.id.ivClear3, "field 'ivClear3' and method 'onClick'");
            target2.ivClear3 = (MryAlphaImageView) Utils.castView(view5, R.id.ivClear3, "field 'ivClear3'", MryAlphaImageView.class);
            this.view7f0901d4 = view5;
            view5.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view6 = Utils.findRequiredView(source, R.id.ivPwdShow3, "field 'ivPwdShow3' and method 'onClick'");
            target2.ivPwdShow3 = (MryAlphaImageView) Utils.castView(view6, R.id.ivPwdShow3, "field 'ivPwdShow3'", MryAlphaImageView.class);
            this.view7f0901f8 = view6;
            view6.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
        }

        public void unbind() {
            StepThree target2 = this.target;
            if (target2 != null) {
                this.target = null;
                target2.etPwd1Parent = null;
                target2.etPwd1 = null;
                target2.ivClear1 = null;
                target2.ivPwdShow1 = null;
                target2.etPwd2Parent = null;
                target2.etPwd2 = null;
                target2.ivClear2 = null;
                target2.ivPwdShow2 = null;
                target2.etPwd3Parent = null;
                target2.etPwd3 = null;
                target2.ivClear3 = null;
                target2.ivPwdShow3 = null;
                this.view7f0901d2.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d2 = null;
                this.view7f0901f6.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f6 = null;
                this.view7f0901d3.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d3 = null;
                this.view7f0901f7.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f7 = null;
                this.view7f0901d4.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d4 = null;
                this.view7f0901f8.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f8 = null;
                super.unbind();
                return;
            }
            throw new IllegalStateException("Bindings already cleared.");
        }
    }

    public LoginPasswordContronllerActivity(int type) {
        this(UserConfig.selectedAccount, type, (Bundle) null);
    }

    public LoginPasswordContronllerActivity(int type, Bundle args) {
        this(UserConfig.selectedAccount, type, args);
    }

    public LoginPasswordContronllerActivity(int account, int type, Bundle args) {
        super(account, args);
        Bundle args2 = getArguments();
        args2 = args2 == null ? new Bundle() : args2;
        args2.putInt("contronllerType", type);
        setArguments(args2);
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.getBackupIpStatus);
        if (getArguments() != null) {
            int i = getArguments().getInt("contronllerType");
            this.contronllerType = i;
            if (i == 1 && getArguments().getString("completedPhoneNumber") == null) {
                if (!BuildVars.LOGS_ENABLED) {
                    return false;
                }
                FileLog.e("LoginPasswordContronllerActivity ===> you must transfer user's completed phone number when you want modify password or set new password.");
                return false;
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.getBackupIpStatus);
    }

    /* access modifiers changed from: protected */
    public void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBarContainer = new FrameLayout(getParentActivity());
        this.actionBarContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.rootView.addView(this.actionBarContainer, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBarContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1 && LoginPasswordContronllerActivity.this.onBackPressed()) {
                    LoginPasswordContronllerActivity.this.finishFragment();
                }
            }
        });
        TextView textView = new TextView(getParentActivity());
        this.backupIpAddressLog = textView;
        textView.setTextColor(getParentActivity().getResources().getColor(R.color.black));
        this.backupIpAddressLog.setTextSize(1, 10.0f);
        ((FrameLayout) this.fragmentView).addView(this.backupIpAddressLog, LayoutHelper.createFrame(-1, -1, 16, 160, 16, 16));
    }

    /* access modifiers changed from: protected */
    public void initView() {
        int i = this.contronllerType;
        if (i == 0 || i == 1) {
            this.pages = new LoginContronllerBaseActivity.ThisView[3];
        } else {
            this.pages = new LoginContronllerBaseActivity.ThisView[1];
        }
        toPage(0, false, getArguments(), false);
    }

    /* access modifiers changed from: protected */
    public void initPages(int newPageIndex) {
        if (this.pages[newPageIndex] == null) {
            this.pages[newPageIndex] = new StepThree(getParentActivity());
        }
    }

    /* access modifiers changed from: protected */
    public void setAcitonBar(int newPageIndex, LoginContronllerBaseActivity.ThisView thisView) {
        super.setAcitonBar(newPageIndex, thisView);
        if (this.contronllerType != 1 || this.currentViewIndex == 2) {
            this.actionBar.setTitle((CharSequence) null);
        } else {
            this.actionBar.setTitle(thisView.getHeaderName());
        }
    }

    public void onResume() {
        super.onResume();
        StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView;
        if (id == NotificationCenter.getBackupIpStatus && (textView = this.backupIpAddressLog) != null) {
            textView.setText(args[0] + "（" + AndroidUtilities.getVersionName(getParentActivity()) + "）");
        }
    }

    public class StepThree extends LoginContronllerBaseActivity.ThisView {
        private boolean et1IsHide = true;
        private boolean et2IsHide = true;
        private boolean et3IsHide = true;
        @BindView(2131296582)
        MryEditText etPwd1;
        @BindView(2131296583)
        View etPwd1Parent;
        @BindView(2131296584)
        MryEditText etPwd2;
        @BindView(2131296585)
        View etPwd2Parent;
        @BindView(2131296586)
        MryEditText etPwd3;
        @BindView(2131296587)
        View etPwd3Parent;
        private TextWatcher etWatcher1 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                StepThree.this.changeBtnState();
            }
        };
        private TextWatcher etWatcher2 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                StepThree.this.changeBtnState();
            }
        };
        private TextWatcher etWatcher3 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                StepThree.this.changeBtnState();
            }
        };
        @BindView(2131296722)
        MryAlphaImageView ivClear1;
        @BindView(2131296723)
        MryAlphaImageView ivClear2;
        @BindView(2131296724)
        MryAlphaImageView ivClear3;
        @BindView(2131296758)
        MryAlphaImageView ivPwdShow1;
        @BindView(2131296759)
        MryAlphaImageView ivPwdShow2;
        @BindView(2131296760)
        MryAlphaImageView ivPwdShow3;

        public StepThree(Context context) {
            super(context);
            addView(LayoutInflater.from(LoginPasswordContronllerActivity.this.getParentActivity()).inflate(R.layout.activity_forget_login_pwd3, (ViewGroup) null, false), LayoutHelper.createLinear(-1, -1));
            initView();
        }

        /* access modifiers changed from: protected */
        public void initView() {
            super.initView();
            this.etPwd1Parent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_windowBackgroundGray)));
            this.etPwd2Parent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_windowBackgroundGray)));
            this.etPwd3Parent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_windowBackgroundGray)));
            this.etPwd1.addTextChangedListener(this.etWatcher1);
            this.etPwd2.addTextChangedListener(this.etWatcher2);
            this.etPwd3.addTextChangedListener(this.etWatcher3);
            TLRPC.User user = MessagesController.getInstance(LoginPasswordContronllerActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(LoginPasswordContronllerActivity.this.currentAccount).getClientUserId()));
            PrintStream printStream = System.out;
            printStream.println(String.format("user:" + user.username, new Object[0]));
            changeBtnState();
        }

        public void setParams(Bundle params, boolean restore) {
            super.setParams(params, restore);
            if (params != null && LoginPasswordContronllerActivity.this.contronllerType == 2) {
                this.tvTitle.setText(LocaleController.getString(R.string.SetNewPwd));
            }
        }

        public void onShowEnd() {
            super.onShowEnd();
            this.etPwd1.setFocusable(true);
            this.etPwd1.setFocusableInTouchMode(true);
            this.etPwd1.requestFocus();
            MryEditText mryEditText = this.etPwd1;
            mryEditText.setSelection(mryEditText.length());
            AndroidUtilities.showKeyboard(this.etPwd1);
        }

        /* access modifiers changed from: package-private */
        @OnClick({2131296722, 2131296723, 2131296724, 2131296758, 2131296759, 2131296760})
        public void onClick(View v) {
            super.onClick(v);
            int id = v.getId();
            switch (id) {
                case R.id.ivClear1 /*2131296722*/:
                    this.etPwd1.setText("");
                    return;
                case R.id.ivClear2 /*2131296723*/:
                    this.etPwd2.setText("");
                    return;
                case R.id.ivClear3 /*2131296724*/:
                    this.etPwd3.setText("");
                    return;
                default:
                    switch (id) {
                        case R.id.ivPwdShow1 /*2131296758*/:
                            boolean z = !this.et1IsHide;
                            this.et1IsHide = z;
                            if (z) {
                                this.ivPwdShow1.setImageResource(R.mipmap.eye_close);
                                this.etPwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            } else {
                                this.ivPwdShow1.setImageResource(R.mipmap.eye_open);
                                this.etPwd1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }
                            this.etPwd1.setSelectionEnd();
                            return;
                        case R.id.ivPwdShow2 /*2131296759*/:
                            boolean z2 = !this.et2IsHide;
                            this.et2IsHide = z2;
                            if (z2) {
                                this.ivPwdShow2.setImageResource(R.mipmap.eye_close);
                                this.etPwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            } else {
                                this.ivPwdShow2.setImageResource(R.mipmap.eye_open);
                                this.etPwd2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }
                            this.etPwd2.setSelectionEnd();
                            return;
                        case R.id.ivPwdShow3 /*2131296760*/:
                            boolean z3 = !this.et3IsHide;
                            this.et3IsHide = z3;
                            if (z3) {
                                this.ivPwdShow3.setImageResource(R.mipmap.eye_close);
                                this.etPwd3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            } else {
                                this.ivPwdShow3.setImageResource(R.mipmap.eye_open);
                                this.etPwd3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }
                            this.etPwd3.setSelectionEnd();
                            return;
                        default:
                            return;
                    }
            }
        }

        /* access modifiers changed from: protected */
        public boolean checkEnterInfo(boolean sendSmsCode, boolean showErrorToast, boolean toNextStep) {
            if (this.etPwd1.getText() == null || this.etPwd1.getText().length() == 0) {
                if (showErrorToast) {
                    ToastUtils.show((int) R.string.PleaseEnterYourOldPwd);
                }
                return false;
            } else if (this.etPwd2.getText() == null || this.etPwd2.getText().length() == 0) {
                if (showErrorToast) {
                    ToastUtils.show((int) R.string.PleaseEnterANewPassword);
                }
                return false;
            } else if (this.etPwd3.getText() == null || this.etPwd3.getText().length() == 0) {
                if (showErrorToast) {
                    ToastUtils.show((int) R.string.PaymentPasswordReEnter);
                }
                return false;
            } else if (!LoginPasswordContronllerActivity.this.checkPasswordRule(this.etPwd1, showErrorToast) || !LoginPasswordContronllerActivity.this.checkPasswordRule(this.etPwd2, showErrorToast) || !LoginPasswordContronllerActivity.this.checkPasswordRule(this.etPwd3, showErrorToast)) {
                return false;
            } else {
                if (this.etPwd2.getText().toString().trim().compareTo(this.etPwd3.getText().toString().trim()) != 0) {
                    if (showErrorToast) {
                        ToastUtils.show((int) R.string.DiffNewPayPasswordTips);
                    }
                    return false;
                } else if (!toNextStep) {
                    return true;
                } else {
                    resetLoginPwd();
                    return true;
                }
            }
        }

        /* access modifiers changed from: private */
        public void changeBtnState() {
            if (this.etPwd1.length() > 0) {
                this.ivClear1.setVisibility(0);
                if (this.etPwd2.length() > 0) {
                    this.ivClear2.setVisibility(0);
                    if (this.etPwd3.length() > 0) {
                        this.ivClear3.setVisibility(0);
                        this.btn.setEnabled(true);
                        return;
                    }
                    this.ivClear3.setVisibility(8);
                } else {
                    this.ivClear2.setVisibility(8);
                }
            } else {
                this.ivClear1.setVisibility(8);
            }
            this.btn.setEnabled(false);
        }

        private void resetLoginPwd() {
            TLObject req;
            int i = 0;
            this.btn.setEnabled(false);
            int flags = 0;
            if (LoginPasswordContronllerActivity.this.contronllerType == 2) {
                req = new TLRPCLogin.TL_auth_LoginPasswordSet();
                ((TLRPCLogin.TL_auth_LoginPasswordSet) req).password = Base64.encodeToString(MD5.md5(this.etPwd1.getText().toString().trim()).getBytes(), 0);
                ((TLRPCLogin.TL_auth_LoginPasswordSet) req).password = ((TLRPCLogin.TL_auth_LoginPasswordSet) req).password.replaceAll("\n", "");
            } else {
                req = new TLRPCLogin.TL_auth_LoginPasswordReset_v2();
                String current_pwd = Base64.encodeToString(MD5.md5(this.etPwd1.getText().toString().trim()).getBytes(), 0).replaceAll("\n", "");
                String new_pwd = Base64.encodeToString(MD5.md5(this.etPwd2.getText().toString().trim()).getBytes(), 0).replaceAll("\n", "");
                ((TLRPCLogin.TL_auth_LoginPasswordReset_v2) req).current_pwd_hash = current_pwd;
                ((TLRPCLogin.TL_auth_LoginPasswordReset_v2) req).new_pwd_hash = new_pwd;
                if (LoginPasswordContronllerActivity.this.contronllerType != 1) {
                    i = 10;
                }
                flags = i;
            }
            AlertDialog progressDialog = new AlertDialog(LoginPasswordContronllerActivity.this.getParentActivity(), 3);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    LoginPasswordContronllerActivity.StepThree.this.lambda$resetLoginPwd$0$LoginPasswordContronllerActivity$StepThree(dialogInterface);
                }
            });
            LoginPasswordContronllerActivity.this.showDialog(progressDialog);
            ConnectionsManager.getInstance(LoginPasswordContronllerActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(LoginPasswordContronllerActivity.this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginPasswordContronllerActivity.StepThree.this.lambda$resetLoginPwd$2$LoginPasswordContronllerActivity$StepThree(this.f$1, tLObject, tL_error);
                }
            }, flags), LoginPasswordContronllerActivity.this.classGuid);
        }

        public /* synthetic */ void lambda$resetLoginPwd$0$LoginPasswordContronllerActivity$StepThree(DialogInterface dialog) {
            LoginPasswordContronllerActivity.this.getConnectionsManager().cancelRequestsForGuid(LoginPasswordContronllerActivity.this.classGuid);
            this.btn.setEnabled(true);
        }

        public /* synthetic */ void lambda$resetLoginPwd$2$LoginPasswordContronllerActivity$StepThree(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLRPC.TL_error f$2;
                private final /* synthetic */ TLObject f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LoginPasswordContronllerActivity.StepThree.this.lambda$null$1$LoginPasswordContronllerActivity$StepThree(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$LoginPasswordContronllerActivity$StepThree(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            this.btn.setEnabled(true);
            if (error != null) {
                LoginPasswordContronllerActivity.this.parseError(error, this.params.getString("completedPhoneNumber"));
            } else if (!(response instanceof TLRPC.Bool)) {
            } else {
                if (response instanceof TLRPC.TL_boolTrue) {
                    if (LoginPasswordContronllerActivity.this.contronllerType == 2) {
                        ToastUtils.show((int) R.string.TheLoginPasswordIsSetSuccessfully);
                        if (LoginPasswordContronllerActivity.this.newAccount) {
                            LoginPasswordContronllerActivity.this.newAccount = false;
                            ((LaunchActivity) LoginPasswordContronllerActivity.this.getParentActivity()).switchToAccount(LoginPasswordContronllerActivity.this.currentAccount, true);
                            LoginPasswordContronllerActivity.this.finishFragment();
                        } else if (!LoginPasswordContronllerActivity.this.canBack) {
                            LoginPasswordContronllerActivity.this.presentFragment(new IndexActivity(), true);
                            NotificationCenter.getInstance(LoginPasswordContronllerActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                        } else {
                            LoginPasswordContronllerActivity.this.finishFragment();
                            NotificationCenter.getInstance(LoginPasswordContronllerActivity.this.currentAccount).postNotificationName(NotificationCenter.loginPasswordSetSuccess, new Object[0]);
                        }
                    } else if (LoginPasswordContronllerActivity.this.contronllerType == 0) {
                        ToastUtils.show((int) R.string.PwdResetSuccessful);
                        LoginPasswordContronllerActivity.this.finishFragment();
                    } else {
                        LoginPasswordContronllerActivity.this.finishFragment();
                    }
                } else if (LoginPasswordContronllerActivity.this.contronllerType == 2) {
                    ToastUtils.show((int) R.string.TheLoginPasswordIsSetFailed);
                } else if (LoginPasswordContronllerActivity.this.contronllerType == 0) {
                    ToastUtils.show((int) R.string.PwdResetFailed);
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            super.saveStateParams(bundle);
            String pwd1 = this.etPwd1.getText() + "";
            String pwd2 = this.etPwd2.getText() + "";
            if (!TextUtils.isEmpty(pwd1)) {
                bundle.putString("pwd1", pwd1);
            }
            if (!TextUtils.isEmpty(pwd2)) {
                bundle.putString("pwd2", pwd2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            MryEditText mryEditText;
            MryEditText mryEditText2;
            super.restoreStateParams(bundle);
            String pwd1 = bundle.getString("pwd1");
            String pwd2 = bundle.getString("pwd2");
            if (!TextUtils.isEmpty(pwd1) && (mryEditText2 = this.etPwd1) != null) {
                mryEditText2.setText(pwd1);
            }
            if (!TextUtils.isEmpty(pwd2) && (mryEditText = this.etPwd2) != null) {
                mryEditText.setText(pwd1);
            }
        }

        public void onDestroyActivity() {
            TextWatcher textWatcher;
            TextWatcher textWatcher2;
            TextWatcher textWatcher3;
            MryEditText mryEditText = this.etPwd1;
            if (!(mryEditText == null || (textWatcher3 = this.etWatcher1) == null)) {
                mryEditText.removeTextChangedListener(textWatcher3);
                this.etWatcher1 = null;
            }
            MryEditText mryEditText2 = this.etPwd2;
            if (!(mryEditText2 == null || (textWatcher2 = this.etWatcher2) == null)) {
                mryEditText2.removeTextChangedListener(textWatcher2);
                this.etWatcher2 = null;
            }
            MryEditText mryEditText3 = this.etPwd3;
            if (!(mryEditText3 == null || (textWatcher = this.etWatcher3) == null)) {
                mryEditText3.removeTextChangedListener(textWatcher);
                this.etWatcher3 = null;
            }
            super.onDestroyActivity();
        }
    }
}
