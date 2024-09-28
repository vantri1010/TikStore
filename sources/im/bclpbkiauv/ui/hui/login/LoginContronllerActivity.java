package im.bclpbkiauv.ui.hui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.serenegiant.uvccamera.BuildConfig;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.RegexUtils;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NetworkConfig;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.translate.MD5;
import im.bclpbkiauv.ui.ExternalActionActivity;
import im.bclpbkiauv.ui.IndexActivity;
import im.bclpbkiauv.ui.IpChangeActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;
import im.bclpbkiauv.ui.hui.login.LoginContronllerBaseActivity;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryCheckBox;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.utils.DeviceUtils;
import im.bclpbkiauv.utils.FingerprintUtil;
import im.bclpbkiauv.utils.VerifyCodeUtils;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginContronllerActivity extends LoginContronllerBaseActivity implements NotificationCenter.NotificationCenterDelegate {
    public static final String REGEX_USERNAME = "^[\\w]{5,32}(?<!_)$";
    public TextView backupIpAddressLog;
    private int contronllerType;
    protected boolean mIsLoginSuccess;
    private ActionBarMenuItem rightMenuItem;
    private VideoView videoView;

    public class TView_ViewBinding extends LoginContronllerBaseActivity.ThisView_ViewBinding {
        private TView target;
        private View view7f090096;
        private View view7f0901db;

        public TView_ViewBinding(TView target2) {
            this(target2, target2);
        }

        public TView_ViewBinding(final TView target2, View source) {
            super(target2, source);
            this.target = target2;
            target2.etPhoneNumberParent = Utils.findRequiredView(source, R.id.etPhoneNumberParent, "field 'etPhoneNumberParent'");
            target2.etPhoneNumber = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPhoneNumber, "field 'etPhoneNumber'", MryEditText.class);
            View view = Utils.findRequiredView(source, R.id.ivClearPhoneNumber, "field 'ivClearPhoneNumber' and method 'onClick'");
            target2.ivClearPhoneNumber = (MryAlphaImageView) Utils.castView(view, R.id.ivClearPhoneNumber, "field 'ivClearPhoneNumber'", MryAlphaImageView.class);
            this.view7f0901db = view;
            view.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.tvPhoneNumberInvalidTips = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPhoneNumberInvalidTips, "field 'tvPhoneNumberInvalidTips'", MryTextView.class);
            target2.touristBtn = (MryRoundButton) Utils.findRequiredViewAsType(source, R.id.touristBtn, "field 'touristBtn'", MryRoundButton.class);
            target2.etGoogleCode = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etGoogleCode, "field 'etGoogleCode'", MryEditText.class);
            target2.etGoogleCodedParent = Utils.findRequiredView(source, R.id.etGoogleCodedParent, "field 'etGoogleCodedParent'");
            target2.ivClearGoogleCode = (MryAlphaImageView) Utils.findRequiredViewAsType(source, R.id.ivClearGoogleCode, "field 'ivClearGoogleCode'", MryAlphaImageView.class);
            View view2 = Utils.findRequiredView(source, R.id.btn, "method 'onClick'");
            this.view7f090096 = view2;
            view2.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
        }

        public void unbind() {
            TView target2 = this.target;
            if (target2 != null) {
                this.target = null;
                target2.etPhoneNumberParent = null;
                target2.etPhoneNumber = null;
                target2.ivClearPhoneNumber = null;
                target2.tvPhoneNumberInvalidTips = null;
                target2.touristBtn = null;
                target2.etGoogleCode = null;
                target2.etGoogleCodedParent = null;
                target2.ivClearGoogleCode = null;
                this.view7f0901db.setOnClickListener((View.OnClickListener) null);
                this.view7f0901db = null;
                this.view7f090096.setOnClickListener((View.OnClickListener) null);
                this.view7f090096 = null;
                super.unbind();
                return;
            }
            throw new IllegalStateException("Bindings already cleared.");
        }
    }

    public class PasswordView_ViewBinding extends TView_ViewBinding {
        private PasswordView target;
        private View view7f0901d5;
        private View view7f0901d9;
        private View view7f0901f6;
        private View view7f090294;
        private View view7f09048c;
        private View view7f0904ed;
        private View view7f090504;
        private View view7f090584;

        public PasswordView_ViewBinding(PasswordView target2) {
            this(target2, target2);
        }

        public PasswordView_ViewBinding(final PasswordView target2, View source) {
            super(target2, source);
            this.target = target2;
            target2.etPasswordParent1 = Utils.findRequiredView(source, R.id.etPasswordParent1, "field 'etPasswordParent1'");
            target2.etPassword = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPassword1, "field 'etPassword'", MryEditText.class);
            View view = Utils.findRequiredView(source, R.id.ivClearPassword1, "field 'ivClearPassword' and method 'onClick'");
            target2.ivClearPassword = (MryAlphaImageView) Utils.castView(view, R.id.ivClearPassword1, "field 'ivClearPassword'", MryAlphaImageView.class);
            this.view7f0901d9 = view;
            view.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view2 = Utils.findRequiredView(source, R.id.ivPwdShow1, "field 'ivPwdShow' and method 'onClick'");
            target2.ivPwdShow = (MryAlphaImageView) Utils.castView(view2, R.id.ivPwdShow1, "field 'ivPwdShow'", MryAlphaImageView.class);
            this.view7f0901f6 = view2;
            view2.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.tvPasswordInvalidTips1 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPasswordInvalidTips1, "field 'tvPasswordInvalidTips1'", MryTextView.class);
            View view3 = Utils.findRequiredView(source, R.id.tvForgotPassword, "field 'tvForgotPassword' and method 'onClick'");
            target2.tvForgotPassword = (MryTextView) Utils.castView(view3, R.id.tvForgotPassword, "field 'tvForgotPassword'", MryTextView.class);
            this.view7f0904ed = view3;
            view3.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view4 = Utils.findRequiredView(source, R.id.llPrivacyAgreement, "field 'llPrivacyAgreement' and method 'onClick'");
            target2.llPrivacyAgreement = view4;
            this.view7f090294 = view4;
            view4.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.cbPrivacyAgreement = (MryCheckBox) Utils.findRequiredViewAsType(source, R.id.cbPrivacyAgreement, "field 'cbPrivacyAgreement'", MryCheckBox.class);
            target2.tvPrivacyAgreement = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPrivacyAgreement, "field 'tvPrivacyAgreement'", MryTextView.class);
            View view5 = Utils.findRequiredView(source, R.id.tvLogin, "method 'onClick'");
            this.view7f090504 = view5;
            view5.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view6 = Utils.findRequiredView(source, R.id.tvTitle, "method 'onClick'");
            this.view7f090584 = view6;
            view6.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view7 = Utils.findRequiredView(source, R.id.touristBtn, "method 'onClick'");
            this.view7f09048c = view7;
            view7.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view8 = Utils.findRequiredView(source, R.id.ivClearGoogleCode, "method 'onClick'");
            this.view7f0901d5 = view8;
            view8.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
        }

        public void unbind() {
            PasswordView target2 = this.target;
            if (target2 != null) {
                this.target = null;
                target2.etPasswordParent1 = null;
                target2.etPassword = null;
                target2.ivClearPassword = null;
                target2.ivPwdShow = null;
                target2.tvPasswordInvalidTips1 = null;
                target2.tvForgotPassword = null;
                target2.llPrivacyAgreement = null;
                target2.cbPrivacyAgreement = null;
                target2.tvPrivacyAgreement = null;
                this.view7f0901d9.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d9 = null;
                this.view7f0901f6.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f6 = null;
                this.view7f0904ed.setOnClickListener((View.OnClickListener) null);
                this.view7f0904ed = null;
                this.view7f090294.setOnClickListener((View.OnClickListener) null);
                this.view7f090294 = null;
                this.view7f090504.setOnClickListener((View.OnClickListener) null);
                this.view7f090504 = null;
                this.view7f090584.setOnClickListener((View.OnClickListener) null);
                this.view7f090584 = null;
                this.view7f09048c.setOnClickListener((View.OnClickListener) null);
                this.view7f09048c = null;
                this.view7f0901d5.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d5 = null;
                super.unbind();
                return;
            }
            throw new IllegalStateException("Bindings already cleared.");
        }
    }

    public class RegisterView_ViewBinding extends TView_ViewBinding {
        private RegisterView target;
        private View view7f0901d7;
        private View view7f0901d9;
        private View view7f0901da;
        private View view7f0901f6;
        private View view7f0901f7;
        private View view7f090294;
        private View view7f09048c;
        private View view7f0904ed;
        private View view7f090504;

        public RegisterView_ViewBinding(RegisterView target2) {
            this(target2, target2);
        }

        public RegisterView_ViewBinding(final RegisterView target2, View source) {
            super(target2, source);
            this.target = target2;
            target2.etPassword2Root = Utils.findRequiredView(source, R.id.etPassword2Root, "field 'etPassword2Root'");
            target2.etPasswordParent1 = Utils.findRequiredView(source, R.id.etPasswordParent1, "field 'etPasswordParent1'");
            target2.etPasswordParent2 = Utils.findRequiredView(source, R.id.etPasswordParent2, "field 'etPasswordParent2'");
            target2.etInviteCodedParent = Utils.findRequiredView(source, R.id.etInviteCodedParent, "field 'etInviteCodedParent'");
            target2.etPassword1 = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPassword1, "field 'etPassword1'", MryEditText.class);
            target2.etPassword2 = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etPassword2, "field 'etPassword2'", MryEditText.class);
            target2.etInviteCode = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etInviteCode, "field 'etInviteCode'", MryEditText.class);
            View view = Utils.findRequiredView(source, R.id.ivClearPassword1, "field 'ivClearPassword1' and method 'onClick'");
            target2.ivClearPassword1 = (MryAlphaImageView) Utils.castView(view, R.id.ivClearPassword1, "field 'ivClearPassword1'", MryAlphaImageView.class);
            this.view7f0901d9 = view;
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
            View view3 = Utils.findRequiredView(source, R.id.ivClearPassword2, "field 'ivClearPassword2' and method 'onClick'");
            target2.ivClearPassword2 = (MryAlphaImageView) Utils.castView(view3, R.id.ivClearPassword2, "field 'ivClearPassword2'", MryAlphaImageView.class);
            this.view7f0901da = view3;
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
            View view5 = Utils.findRequiredView(source, R.id.ivClearInviteCode, "field 'ivClearInviteCode' and method 'onClick'");
            target2.ivClearInviteCode = (MryAlphaImageView) Utils.castView(view5, R.id.ivClearInviteCode, "field 'ivClearInviteCode'", MryAlphaImageView.class);
            this.view7f0901d7 = view5;
            view5.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.tvPasswordInvalidTips1 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPasswordInvalidTips1, "field 'tvPasswordInvalidTips1'", MryTextView.class);
            target2.tvPasswordInvalidTips2 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPasswordInvalidTips2, "field 'tvPasswordInvalidTips2'", MryTextView.class);
            View view6 = Utils.findRequiredView(source, R.id.tvForgotPassword, "field 'tvForgotPassword' and method 'onClick'");
            target2.tvForgotPassword = (MryTextView) Utils.castView(view6, R.id.tvForgotPassword, "field 'tvForgotPassword'", MryTextView.class);
            this.view7f0904ed = view6;
            view6.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view7 = Utils.findRequiredView(source, R.id.tvLogin, "field 'tvLogin' and method 'onClick'");
            target2.tvLogin = (MryTextView) Utils.castView(view7, R.id.tvLogin, "field 'tvLogin'", MryTextView.class);
            this.view7f090504 = view7;
            view7.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            View view8 = Utils.findRequiredView(source, R.id.llPrivacyAgreement, "field 'llPrivacyAgreement' and method 'onClick'");
            target2.llPrivacyAgreement = view8;
            this.view7f090294 = view8;
            view8.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
            target2.cbPrivacyAgreement = (MryCheckBox) Utils.findRequiredViewAsType(source, R.id.cbPrivacyAgreement, "field 'cbPrivacyAgreement'", MryCheckBox.class);
            target2.tvPrivacyAgreement = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvPrivacyAgreement, "field 'tvPrivacyAgreement'", MryTextView.class);
            target2.ivCode = (ImageFilterView) Utils.findRequiredViewAsType(source, R.id.ivCode, "field 'ivCode'", ImageFilterView.class);
            target2.etCode = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etCode, "field 'etCode'", MryEditText.class);
            target2.llImageCode = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_image_code, "field 'llImageCode'", LinearLayout.class);
            target2.logoBg = (MryAlphaImageView) Utils.findRequiredViewAsType(source, R.id.logoBg, "field 'logoBg'", MryAlphaImageView.class);
            View view9 = Utils.findRequiredView(source, R.id.touristBtn, "method 'onClick'");
            this.view7f09048c = view9;
            view9.setOnClickListener(new DebouncingOnClickListener() {
                public void doClick(View p0) {
                    target2.onClick(p0);
                }
            });
        }

        public void unbind() {
            RegisterView target2 = this.target;
            if (target2 != null) {
                this.target = null;
                target2.etPassword2Root = null;
                target2.etPasswordParent1 = null;
                target2.etPasswordParent2 = null;
                target2.etInviteCodedParent = null;
                target2.etPassword1 = null;
                target2.etPassword2 = null;
                target2.etInviteCode = null;
                target2.ivClearPassword1 = null;
                target2.ivPwdShow1 = null;
                target2.ivClearPassword2 = null;
                target2.ivPwdShow2 = null;
                target2.ivClearInviteCode = null;
                target2.tvPasswordInvalidTips1 = null;
                target2.tvPasswordInvalidTips2 = null;
                target2.tvForgotPassword = null;
                target2.tvLogin = null;
                target2.llPrivacyAgreement = null;
                target2.cbPrivacyAgreement = null;
                target2.tvPrivacyAgreement = null;
                target2.ivCode = null;
                target2.etCode = null;
                target2.llImageCode = null;
                target2.logoBg = null;
                this.view7f0901d9.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d9 = null;
                this.view7f0901f6.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f6 = null;
                this.view7f0901da.setOnClickListener((View.OnClickListener) null);
                this.view7f0901da = null;
                this.view7f0901f7.setOnClickListener((View.OnClickListener) null);
                this.view7f0901f7 = null;
                this.view7f0901d7.setOnClickListener((View.OnClickListener) null);
                this.view7f0901d7 = null;
                this.view7f0904ed.setOnClickListener((View.OnClickListener) null);
                this.view7f0904ed = null;
                this.view7f090504.setOnClickListener((View.OnClickListener) null);
                this.view7f090504 = null;
                this.view7f090294.setOnClickListener((View.OnClickListener) null);
                this.view7f090294 = null;
                this.view7f09048c.setOnClickListener((View.OnClickListener) null);
                this.view7f09048c = null;
                super.unbind();
                return;
            }
            throw new IllegalStateException("Bindings already cleared.");
        }
    }

    public LoginContronllerActivity() {
        this(UserConfig.selectedAccount, (Bundle) null);
    }

    public LoginContronllerActivity(int account) {
        this(account, (Bundle) null);
    }

    public LoginContronllerActivity(int account, Bundle args) {
        super(account, args);
    }

    public boolean onFragmentCreate() {
        boolean tag = super.onFragmentCreate();
        if (tag) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogIn);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.getBackupIpStatus);
        }
        return tag;
    }

    /* access modifiers changed from: protected */
    public void initActionBar() {
        this.rightMenuItem = null;
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackgroundColor(0);
        this.actionBar.setItemsBackgroundColor(0, false);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setCastShadows(false);
        this.actionBarContainer = new FrameLayout(getParentActivity());
        this.actionBarContainer.setBackgroundColor(0);
        this.rootView.addView(this.actionBarContainer, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBarContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 51));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    if (LoginContronllerActivity.this.actionBar.getBackButton() != null && LoginContronllerActivity.this.actionBar.getBackButton().getVisibility() == 0 && LoginContronllerActivity.this.onBackPressed()) {
                        LoginContronllerActivity.this.finishFragment();
                    }
                } else if (id != 1) {
                } else {
                    if (LoginContronllerActivity.this.currentViewIndex == 0) {
                        LoginContronllerActivity loginContronllerActivity = LoginContronllerActivity.this;
                        loginContronllerActivity.toPage(1, true, loginContronllerActivity.pages[LoginContronllerActivity.this.currentViewIndex].getParams(), false);
                        return;
                    }
                    LoginContronllerActivity.this.onBackPressed();
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
        initVideoView();
        this.pages = new LoginContronllerBaseActivity.ThisView[2];
        toPage(0, false, getArguments(), false);
    }

    /* access modifiers changed from: protected */
    public void initPages(int newPageIndex) {
        if (this.pages[newPageIndex] != null) {
            return;
        }
        if (newPageIndex == 0) {
            this.pages[newPageIndex] = new RegisterView(getParentActivity());
        } else {
            this.pages[newPageIndex] = new PasswordView(getParentActivity());
        }
    }

    /* access modifiers changed from: protected */
    public void setAcitonBar(int newPageIndex, LoginContronllerBaseActivity.ThisView thisView) {
        super.setAcitonBar(newPageIndex, thisView);
        ActionBarMenuItem actionBarMenuItem = this.rightMenuItem;
        if (actionBarMenuItem == null) {
            ActionBarMenu menu = this.actionBar.createMenu();
            if (newPageIndex == 0) {
                this.rightMenuItem = menu.addItem(1, (CharSequence) LocaleController.getString(R.string.LoginByPassword));
            } else {
                this.rightMenuItem = menu.addItem(1, (CharSequence) LocaleController.getString(R.string.SignUp));
            }
            TextView tvRight = (TextView) this.rightMenuItem.getContentView();
            tvRight.setTextSize(15.0f);
            tvRight.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            return;
        }
        TextView tvRight2 = (TextView) actionBarMenuItem.getContentView();
        if (newPageIndex == 0) {
            tvRight2.setText(LocaleController.getString(R.string.LoginByPassword));
        } else {
            tvRight2.setText(LocaleController.getString(R.string.SignUp));
        }
    }

    /* access modifiers changed from: protected */
    public void initVideoView() {
        this.videoView = new VideoView(getParentActivity()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
            }
        };
        this.rootView.addView(this.videoView, 0, LayoutHelper.createFrame(-1, -1.0f));
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public final void onPrepared(MediaPlayer mediaPlayer) {
                LoginContronllerActivity.this.lambda$initVideoView$0$LoginContronllerActivity(mediaPlayer);
            }
        });
        this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public final void onCompletion(MediaPlayer mediaPlayer) {
                LoginContronllerActivity.this.lambda$initVideoView$1$LoginContronllerActivity(mediaPlayer);
            }
        });
        this.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public final boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                return LoginContronllerActivity.this.lambda$initVideoView$2$LoginContronllerActivity(mediaPlayer, i, i2);
            }
        });
        startPlayVideo();
    }

    public /* synthetic */ void lambda$initVideoView$0$LoginContronllerActivity(MediaPlayer mp) {
        if (this.videoView != null && !isFinishing()) {
            if (this.videoView.getVisibility() != 0) {
                this.videoView.setVisibility(0);
            }
            mp.setVolume(0.0f, 0.0f);
            this.videoView.start();
        }
    }

    public /* synthetic */ void lambda$initVideoView$1$LoginContronllerActivity(MediaPlayer mp) {
        stopPlayVideo();
        startPlayVideo();
    }

    public /* synthetic */ boolean lambda$initVideoView$2$LoginContronllerActivity(MediaPlayer mp, int what, int extra) {
        VideoView videoView2 = this.videoView;
        if (videoView2 == null) {
            return false;
        }
        videoView2.setVisibility(8);
        return false;
    }

    public void onResume() {
        super.onResume();
        startPlayVideo();
        setNavigationBarColor(0);
    }

    /* access modifiers changed from: protected */
    public void setStatusBarTheme() {
        StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), false);
    }

    public void onPause() {
        super.onPause();
        stopPlayVideo();
    }

    /* access modifiers changed from: protected */
    public void startPlayVideo() {
        VideoView videoView2 = this.videoView;
        if (videoView2 != null && !videoView2.isPlaying()) {
            try {
                this.videoView.setVideoURI(Uri.parse("android.resource://" + ApplicationLoader.applicationContext.getPackageName() + "/raw/" + R.raw.login_bg_video));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void stopPlayVideo() {
        VideoView videoView2 = this.videoView;
        if (videoView2 != null) {
            try {
                videoView2.stopPlayback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LoginContronllerActivity.this.lambda$onAuthSuccess$3$LoginContronllerActivity();
            }
        });
        this.mIsLoginSuccess = true;
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = false;
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

    public /* synthetic */ void lambda$onAuthSuccess$3$LoginContronllerActivity() {
        getNotificationCenter().postNotificationName(NotificationCenter.getBackupIpStatus, "server 5");
    }

    /* access modifiers changed from: protected */
    public void clearCurrentState() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        editor.clear();
        editor.apply();
    }

    public void saveSelfArgs(Bundle args) {
        super.saveSelfArgs(args);
        args.putInt("currentIndex", this.currentViewIndex);
        args.putBundle("pageArgs", getArguments());
        Bundle bundle = new Bundle();
        if (this.pages != null) {
            for (int i = 0; i < this.pages.length; i++) {
                LoginContronllerBaseActivity.ThisView t = this.pages[i];
                if (t != null) {
                    t.saveStateParams(bundle);
                    args.putBundle("currentIndexB" + i, bundle);
                }
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        super.restoreSelfArgs(args);
        this.currentViewIndex = args.getInt("currentIndex");
        setArguments(args.getBundle("pageArgs"));
        if (this.pages != null) {
            for (int i = 0; i < this.pages.length; i++) {
                LoginContronllerBaseActivity.ThisView t = this.pages[i];
                if (t != null) {
                    t.restoreStateParams(args.getBundle("currentIndexB" + i));
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView;
        if (id == NotificationCenter.appDidLogIn) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.getBackupIpStatus && (textView = this.backupIpAddressLog) != null) {
            textView.setText(args[0] + "（" + AndroidUtilities.getVersionName(getParentActivity()) + "）");
        }
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogIn);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.getBackupIpStatus);
        super.onFragmentDestroy();
        stopPlayVideo();
        this.videoView = null;
        this.rightMenuItem = null;
    }

    public class PasswordView extends TView {
        @BindView(2131296442)
        MryCheckBox cbPrivacyAgreement;
        private TextWatcher etGoogleCodeWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                PasswordView.this.changeBtnState();
                if (PasswordView.this.ivClearGoogleCode != null) {
                    PasswordView.this.ivClearGoogleCode.setVisibility((s == null || s.length() <= 0) ? 8 : 0);
                }
            }
        };
        @BindView(2131296575)
        MryEditText etPassword;
        @BindView(2131296578)
        View etPasswordParent1;
        private TextWatcher etPasswordWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                PasswordView passwordView = PasswordView.this;
                passwordView.checkShowInputIsInvalidTips(passwordView.etPassword);
                PasswordView.this.changeBtnState();
                if (PasswordView.this.ivClearPassword != null) {
                    PasswordView.this.ivClearPassword.setVisibility((s == null || s.length() <= 0) ? 8 : 0);
                }
            }
        };
        private boolean etPwdIsHide = true;
        @BindView(2131296729)
        MryAlphaImageView ivClearPassword;
        @BindView(2131296758)
        MryAlphaImageView ivPwdShow;
        @BindView(2131296916)
        View llPrivacyAgreement;
        int pressChangeIpCount = 0;
        @BindView(2131297517)
        MryTextView tvForgotPassword;
        @BindView(2131297563)
        MryTextView tvPasswordInvalidTips1;
        @BindView(2131297572)
        MryTextView tvPrivacyAgreement;

        public PasswordView(Context context) {
            super(context);
            addView(LayoutInflater.from(context).inflate(R.layout.activity_login_by_password, (ViewGroup) null, false), LayoutHelper.createFrame(-1, -1.0f));
            initView();
        }

        /* access modifiers changed from: protected */
        public void initView() {
            super.initView();
            this.touristBtn.setVisibility(8);
            this.etPasswordParent1.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            SpanUtils.with(this.tvForgotPassword).append(LocaleController.getString(R.string.ForgetPassword)).setForegroundColor(-1).setUnderline().create();
            this.etPassword.addTextChangedListener(this.etPasswordWatcher);
            System.out.println("------etGoogleCodedParent visible");
            this.etGoogleCodedParent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            this.etGoogleCodedParent.setVisibility(0);
            this.etGoogleCodedParent.setVisibility(8);
        }

        /* access modifiers changed from: protected */
        public void onShowEnd() {
            super.onShowEnd();
        }

        /* access modifiers changed from: package-private */
        @OnClick({2131297517, 2131297540, 2131296729, 2131296758, 2131296916, 2131297668, 2131297420, 2131296725})
        public void onClick(View v) {
            super.onClick(v);
            boolean z = true;
            switch (v.getId()) {
                case R.id.ivClearGoogleCode /*2131296725*/:
                    if (this.etGoogleCode != null) {
                        this.etGoogleCode.setText("");
                        return;
                    }
                    return;
                case R.id.ivClearPassword1 /*2131296729*/:
                    MryEditText mryEditText = this.etPassword;
                    if (mryEditText != null) {
                        mryEditText.setText("");
                        return;
                    }
                    return;
                case R.id.ivPwdShow1 /*2131296758*/:
                    boolean z2 = !this.etPwdIsHide;
                    this.etPwdIsHide = z2;
                    if (z2) {
                        this.ivPwdShow.setImageResource(R.mipmap.eye_close);
                        this.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        this.ivPwdShow.setImageResource(R.mipmap.eye_open);
                        this.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    this.etPassword.setSelectionEnd();
                    return;
                case R.id.llPrivacyAgreement /*2131296916*/:
                    MryCheckBox mryCheckBox = this.cbPrivacyAgreement;
                    mryCheckBox.setChecked(!mryCheckBox.isChecked());
                    return;
                case R.id.tvForgotPassword /*2131297517*/:
                    Bundle args = new Bundle();
                    if (this.etPhoneNumber == null || this.etPhoneNumber.getText() == null || this.etPhoneNumber.length() <= 0) {
                        z = false;
                    }
                    boolean paramsArgs = z;
                    if (paramsArgs) {
                        args.putString("tempPhoneNumber", this.etPhoneNumber.getText().toString().trim());
                    }
                    Bundle bundle = null;
                    if (LoginContronllerActivity.this.newAccount) {
                        LoginContronllerActivity loginContronllerActivity = LoginContronllerActivity.this;
                        int access$200 = loginContronllerActivity.currentAccount;
                        if (paramsArgs) {
                            bundle = args;
                        }
                        loginContronllerActivity.presentFragment(new LoginPasswordContronllerActivity(access$200, 0, bundle));
                        return;
                    }
                    LoginContronllerActivity loginContronllerActivity2 = LoginContronllerActivity.this;
                    if (paramsArgs) {
                        bundle = args;
                    }
                    loginContronllerActivity2.presentFragment(new LoginPasswordContronllerActivity(0, bundle));
                    return;
                case R.id.tvLogin /*2131297540*/:
                    if (LoginContronllerActivity.this.currentViewIndex == 0) {
                        LoginContronllerActivity loginContronllerActivity3 = LoginContronllerActivity.this;
                        loginContronllerActivity3.toPage(1, true, loginContronllerActivity3.pages[LoginContronllerActivity.this.currentViewIndex].getParams(), false);
                        return;
                    }
                    LoginContronllerActivity loginContronllerActivity4 = LoginContronllerActivity.this;
                    loginContronllerActivity4.toPage(0, true, loginContronllerActivity4.pages[LoginContronllerActivity.this.currentViewIndex].getParams(), false);
                    return;
                case R.id.tvTitle /*2131297668*/:
                    int i = this.pressChangeIpCount + 1;
                    this.pressChangeIpCount = i;
                    if (i == 10) {
                        LoginContronllerActivity.this.presentFragment(new IpChangeActivity());
                        this.pressChangeIpCount = 0;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        private void signInByPassword(String account, String pwd) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    LoginContronllerActivity.PasswordView.this.lambda$signInByPassword$0$LoginContronllerActivity$PasswordView();
                }
            });
            this.btn.setEnabled(false);
            TLRPCLogin.TL_auth_SignInByPassword req = new TLRPCLogin.TL_auth_SignInByPassword();
            req.phone_uuid = "";
            req.phone_uuid = DeviceUtils.getDeviceId(ApplicationLoader.applicationContext);
            req.user_name = account;
            req.password_hash = Base64.encodeToString(MD5.md5(pwd).getBytes(), 0);
            req.password_hash = req.password_hash.replaceAll("\n", "");
            AlertDialog progressDialog = new AlertDialog(LoginContronllerActivity.this.getParentActivity(), 3);
            LoginContronllerActivity.this.showDialog(progressDialog);
            Log.e(BuildConfig.BUILD_TYPE, "req22=" + JSONObject.toJSONString(req));
            ConnectionsManager instance = ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount);
            int reqId = ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, req) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLRPCLogin.TL_auth_SignInByPassword f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginContronllerActivity.PasswordView.this.lambda$signInByPassword$2$LoginContronllerActivity$PasswordView(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 10);
            instance.bindRequestToGuid(reqId, LoginContronllerActivity.this.classGuid);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    LoginContronllerActivity.PasswordView.this.lambda$signInByPassword$3$LoginContronllerActivity$PasswordView(this.f$1, dialogInterface);
                }
            });
        }

        public /* synthetic */ void lambda$signInByPassword$0$LoginContronllerActivity$PasswordView() {
            NetworkConfig.serverIndex = 4;
            LoginContronllerActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.getBackupIpStatus, "server 4");
        }

        public /* synthetic */ void lambda$signInByPassword$2$LoginContronllerActivity$PasswordView(AlertDialog progressDialog, TLRPCLogin.TL_auth_SignInByPassword req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, progressDialog, response, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPCLogin.TL_auth_SignInByPassword f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    LoginContronllerActivity.PasswordView.this.lambda$null$1$LoginContronllerActivity$PasswordView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$LoginContronllerActivity$PasswordView(TLRPC.TL_error error, AlertDialog progressDialog, TLObject response, TLRPCLogin.TL_auth_SignInByPassword req) {
            Log.e(BuildConfig.BUILD_TYPE, "response222=" + JSONObject.toJSONString(error));
            progressDialog.dismiss();
            if (error != null) {
                this.btn.setEnabled(true);
                LoginContronllerActivity.this.parseError(error, req.user_name);
            } else if (response instanceof TLRPC.TL_auth_authorizationSignUpRequired) {
                this.btn.setEnabled(true);
            } else if (response instanceof TLRPC.TL_auth_authorization) {
                LoginContronllerActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response);
            }
        }

        public /* synthetic */ void lambda$signInByPassword$3$LoginContronllerActivity$PasswordView(int reqId, DialogInterface dialog) {
            LoginContronllerActivity.this.getConnectionsManager().cancelRequest(reqId, false);
            this.btn.setEnabled(true);
        }

        /* access modifiers changed from: protected */
        public boolean checkEnterInfo(boolean sendSmsCode, boolean showErrorToast, boolean toNextStep) {
            if (!super.checkEnterInfo(sendSmsCode, showErrorToast, toNextStep) || !LoginContronllerActivity.this.checkPasswordRule(this.etPassword, showErrorToast)) {
                return false;
            }
            if (!toNextStep) {
                return true;
            }
            signInByPassword(this.etPhoneNumber.getText().toString().trim(), this.etPassword.getText().toString().trim());
            return true;
        }

        /* access modifiers changed from: protected */
        public void checkShowInputIsInvalidTips(TextView tv) {
            super.checkShowInputIsInvalidTips(tv);
            if (tv != null && this.tvPasswordInvalidTips1 != null && tv == this.etPassword) {
                if (!LoginContronllerActivity.this.checkPasswordRule(tv, false) && this.tvPasswordInvalidTips1.getVisibility() != 0) {
                    this.tvPasswordInvalidTips1.setVisibility(0);
                } else if (LoginContronllerActivity.this.checkPasswordRule(tv, false) && this.tvPasswordInvalidTips1.getVisibility() != 8) {
                    this.tvPasswordInvalidTips1.setVisibility(8);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void changeBtnState() {
            if (!TextUtils.isEmpty(this.etPhoneNumber.getText() + "")) {
                if (!TextUtils.isEmpty(this.etPassword.getText() + "")) {
                    this.btn.setEnabled(true);
                    return;
                }
            }
            this.btn.setEnabled(false);
        }

        public void onDestroyActivity() {
            TextWatcher textWatcher;
            MryEditText mryEditText = this.etPassword;
            if (!(mryEditText == null || (textWatcher = this.etPasswordWatcher) == null)) {
                mryEditText.removeTextChangedListener(textWatcher);
                this.etPasswordWatcher = null;
            }
            super.onDestroyActivity();
        }
    }

    public class RegisterView extends TView {
        @BindView(2131296442)
        MryCheckBox cbPrivacyAgreement;
        @BindView(2131296564)
        MryEditText etCode;
        @BindView(2131296570)
        MryEditText etInviteCode;
        private TextWatcher etInviteCodeWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                RegisterView.this.changeBtnState();
                if (RegisterView.this.ivClearInviteCode != null) {
                    RegisterView.this.ivClearInviteCode.setVisibility((s == null || s.length() <= 0) ? 8 : 0);
                }
            }
        };
        @BindView(2131296571)
        View etInviteCodedParent;
        @BindView(2131296575)
        MryEditText etPassword1;
        @BindView(2131296576)
        MryEditText etPassword2;
        @BindView(2131296577)
        View etPassword2Root;
        @BindView(2131296578)
        View etPasswordParent1;
        @BindView(2131296579)
        View etPasswordParent2;
        private TextWatcher etPasswordWatcher1 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                RegisterView registerView = RegisterView.this;
                registerView.checkShowInputIsInvalidTips(registerView.etPassword1);
                RegisterView.this.changeBtnState();
                if (RegisterView.this.ivClearPassword1 != null) {
                    RegisterView.this.ivClearPassword1.setVisibility((s == null || s.length() <= 0) ? 8 : 0);
                }
            }
        };
        private TextWatcher etPasswordWatcher2 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                RegisterView registerView = RegisterView.this;
                registerView.checkShowInputIsInvalidTips(registerView.etPassword2);
                RegisterView.this.changeBtnState();
                if (RegisterView.this.ivClearPassword2 != null) {
                    RegisterView.this.ivClearPassword2.setVisibility((s == null || s.length() <= 0) ? 8 : 0);
                }
            }
        };
        private boolean etPwdIsHide = true;
        private boolean etPwdIsHide2 = true;
        @BindView(2131296727)
        MryAlphaImageView ivClearInviteCode;
        @BindView(2131296729)
        MryAlphaImageView ivClearPassword1;
        @BindView(2131296730)
        MryAlphaImageView ivClearPassword2;
        @BindView(2131296733)
        ImageFilterView ivCode;
        @BindView(2131296758)
        MryAlphaImageView ivPwdShow1;
        @BindView(2131296759)
        MryAlphaImageView ivPwdShow2;
        @BindView(2131296937)
        LinearLayout llImageCode;
        @BindView(2131296916)
        View llPrivacyAgreement;
        @BindView(2131296969)
        MryAlphaImageView logoBg;
        private int reqCheckUNToken;
        @BindView(2131297517)
        MryTextView tvForgotPassword;
        @BindView(2131297540)
        MryTextView tvLogin;
        @BindView(2131297563)
        MryTextView tvPasswordInvalidTips1;
        @BindView(2131297564)
        MryTextView tvPasswordInvalidTips2;
        @BindView(2131297572)
        MryTextView tvPrivacyAgreement;

        public RegisterView(Context context) {
            super(context);
            addView(LayoutInflater.from(context).inflate(R.layout.activity_login_by_password, (ViewGroup) null, false), LayoutHelper.createFrame(-1, -1.0f));
            initView();
        }

        /* access modifiers changed from: protected */
        public void initView() {
            super.initView();
            this.tvTitle.setText(LocaleController.getString(R.string.SignUp));
            this.btn.setText(LocaleController.getString(R.string.SignUp));
            this.etPasswordParent1.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            this.etPasswordParent2.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            this.etInviteCodedParent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            this.etInviteCodedParent.setVisibility(0);
            this.etPassword1.addTextChangedListener(this.etPasswordWatcher1);
            this.etInviteCodedParent.setVisibility(8);
            this.llImageCode.setVisibility(8);
            this.touristBtn.setVisibility(8);
        }

        private void showCode() {
            this.ivCode.setImageBitmap(VerifyCodeUtils.getInstance().createBitmap());
            if (Build.VERSION.SDK_INT >= 21) {
                this.ivCode.setRound(8.0f);
            }
            this.ivCode.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LoginContronllerActivity.RegisterView.this.lambda$showCode$0$LoginContronllerActivity$RegisterView(view);
                }
            });
            this.btn.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LoginContronllerActivity.RegisterView.this.lambda$showCode$1$LoginContronllerActivity$RegisterView(view);
                }
            });
        }

        public /* synthetic */ void lambda$showCode$0$LoginContronllerActivity$RegisterView(View view) {
            this.ivCode.setImageBitmap(VerifyCodeUtils.getInstance().createBitmap());
        }

        public /* synthetic */ void lambda$showCode$1$LoginContronllerActivity$RegisterView(View view) {
            String trim = this.etCode.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.PleaseInputVerifyCodeHint));
                AndroidUtilities.shakeView(this.etCode, 2.0f, 0);
                return;
            }
            String code = VerifyCodeUtils.getInstance().getCode();
            if (TextUtils.isEmpty(code)) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.VerificationcodeError));
                this.ivCode.setImageBitmap(VerifyCodeUtils.getInstance().createBitmap());
            } else if (trim.toLowerCase().equals(code.toLowerCase())) {
                checkEnterInfo(false, true, true);
            } else {
                AndroidUtilities.shakeView(this.etCode, 2.0f, 0);
                this.ivCode.setImageBitmap(VerifyCodeUtils.getInstance().createBitmap());
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.VerificationcodeError));
            }
        }

        /* access modifiers changed from: package-private */
        @OnClick({2131297517, 2131297540, 2131296729, 2131296758, 2131296730, 2131296727, 2131296759, 2131296916, 2131297420})
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.ivClearInviteCode /*2131296727*/:
                    MryEditText mryEditText = this.etInviteCode;
                    if (mryEditText != null) {
                        mryEditText.setText("");
                        return;
                    }
                    return;
                case R.id.ivClearPassword1 /*2131296729*/:
                    MryEditText mryEditText2 = this.etPassword1;
                    if (mryEditText2 != null) {
                        mryEditText2.setText("");
                        return;
                    }
                    return;
                case R.id.ivClearPassword2 /*2131296730*/:
                    MryEditText mryEditText3 = this.etPassword2;
                    if (mryEditText3 != null) {
                        mryEditText3.setText("");
                        return;
                    }
                    return;
                case R.id.ivPwdShow1 /*2131296758*/:
                    boolean z = !this.etPwdIsHide;
                    this.etPwdIsHide = z;
                    if (z) {
                        this.ivPwdShow1.setImageResource(R.mipmap.eye_close);
                        this.etPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        this.ivPwdShow1.setImageResource(R.mipmap.eye_open);
                        this.etPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    this.etPassword1.setSelectionEnd();
                    return;
                case R.id.ivPwdShow2 /*2131296759*/:
                    boolean z2 = !this.etPwdIsHide2;
                    this.etPwdIsHide2 = z2;
                    if (z2) {
                        this.ivPwdShow2.setImageResource(R.mipmap.eye_close);
                        this.etPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        this.ivPwdShow2.setImageResource(R.mipmap.eye_open);
                        this.etPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    this.etPassword2.setSelectionEnd();
                    return;
                case R.id.llPrivacyAgreement /*2131296916*/:
                    MryCheckBox mryCheckBox = this.cbPrivacyAgreement;
                    mryCheckBox.setChecked(!mryCheckBox.isChecked());
                    return;
                case R.id.touristBtn /*2131297420*/:
                    System.out.println("点击了免密");
                    login2();
                    return;
                case R.id.tvLogin /*2131297540*/:
                    if (LoginContronllerActivity.this.currentViewIndex == 0) {
                        LoginContronllerActivity loginContronllerActivity = LoginContronllerActivity.this;
                        loginContronllerActivity.toPage(1, true, loginContronllerActivity.pages[LoginContronllerActivity.this.currentViewIndex].getParams(), false);
                        return;
                    }
                    LoginContronllerActivity loginContronllerActivity2 = LoginContronllerActivity.this;
                    loginContronllerActivity2.toPage(0, true, loginContronllerActivity2.pages[LoginContronllerActivity.this.currentViewIndex].getParams(), false);
                    return;
                default:
                    return;
            }
        }

        private void login2() {
            System.out.println("自动注册====2");
            String oldFingerprint = DeviceUtils.getDeviceId(LoginContronllerActivity.this.getParentActivity());
            String newFingerprint = FingerprintUtil.getDeviceId(LoginContronllerActivity.this.getParentActivity());
            TLRPCLogin.TL_auth_SignAuto2 req = new TLRPCLogin.TL_auth_SignAuto2();
            req.phone_uuid = DeviceUtils.getDeviceId(LoginContronllerActivity.this.getParentActivity());
            req.company_tag = "Yixin";
            req.device_old = oldFingerprint;
            req.device_new = newFingerprint;
            ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).sendRequest(req, new RequestDelegate(newFingerprint) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginContronllerActivity.RegisterView.this.lambda$login2$3$LoginContronllerActivity$RegisterView(this.f$1, tLObject, tL_error);
                }
            }, 10), LoginContronllerActivity.this.classGuid);
        }

        public /* synthetic */ void lambda$login2$3$LoginContronllerActivity$RegisterView(String newFingerprint, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, response, newFingerprint) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LoginContronllerActivity.RegisterView.this.lambda$null$2$LoginContronllerActivity$RegisterView(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$LoginContronllerActivity$RegisterView(TLRPC.TL_error error, TLObject response, String newFingerprint) {
            if (error != null) {
                LoginContronllerActivity.this.parseError(error, "");
            } else if (!(response instanceof TLRPC.TL_auth_authorizationSignUpRequired) && (response instanceof TLRPC.TL_auth_authorization)) {
                System.out.println("自动注册====3");
                Log.e(BuildConfig.BUILD_TYPE, "response" + JSONObject.toJSONString(response));
                LoginContronllerActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                ApplicationLoader.applicationContext.getSharedPreferences("deviceConfig", 0).edit().putString("device_fingerprint", newFingerprint).commit();
            }
        }

        /* access modifiers changed from: protected */
        public boolean checkEnterInfo(boolean sendSmsCode, boolean showErrorToast, boolean toNextStep) {
            if (!super.checkEnterInfo(sendSmsCode, showErrorToast, toNextStep)) {
                return false;
            }
            if (this.etPassword1.getText() == null || this.etPassword1.getText().length() == 0) {
                if (showErrorToast) {
                    ToastUtils.show((int) R.string.PaymentPasswordEnter);
                }
                return false;
            } else if (!LoginContronllerActivity.this.checkPasswordRule(this.etPassword1, showErrorToast)) {
                return false;
            } else {
                if (!toNextStep) {
                    return true;
                }
                checkUserName();
                return true;
            }
        }

        private void checkUserName() {
            if (this.reqCheckUNToken == 0) {
                if (this.btn != null) {
                    this.btn.setEnabled(false);
                }
                AlertDialog progressDialog = new AlertDialog(LoginContronllerActivity.this.getParentActivity(), 3);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public final void onCancel(DialogInterface dialogInterface) {
                        LoginContronllerActivity.RegisterView.this.lambda$checkUserName$4$LoginContronllerActivity$RegisterView(dialogInterface);
                    }
                });
                LoginContronllerActivity.this.showDialog(progressDialog);
                ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).cancelRequestsForGuid(LoginContronllerActivity.this.classGuid);
                String name = this.etPhoneNumber.getText().toString().trim();
                TLRPC.TL_account_checkUsername req = new TLRPC.TL_account_checkUsername();
                req.username = name;
                ConnectionsManager instance = ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount);
                int sendRequest = ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LoginContronllerActivity.RegisterView.this.lambda$checkUserName$6$LoginContronllerActivity$RegisterView(tLObject, tL_error);
                    }
                }, 10);
                this.reqCheckUNToken = sendRequest;
                instance.bindRequestToGuid(sendRequest, LoginContronllerActivity.this.classGuid);
            }
        }

        public /* synthetic */ void lambda$checkUserName$4$LoginContronllerActivity$RegisterView(DialogInterface dialog) {
            if (this.reqCheckUNToken != 0) {
                ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).cancelRequest(this.reqCheckUNToken, false);
                if (this.btn != null) {
                    this.btn.setEnabled(true);
                }
                this.reqCheckUNToken = 0;
            }
        }

        public /* synthetic */ void lambda$checkUserName$6$LoginContronllerActivity$RegisterView(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, response) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LoginContronllerActivity.RegisterView.this.lambda$null$5$LoginContronllerActivity$RegisterView(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$5$LoginContronllerActivity$RegisterView(TLRPC.TL_error error, TLObject response) {
            LoginContronllerActivity.this.dismissCurrentDialog();
            if (this.btn != null) {
                this.btn.setEnabled(true);
            }
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                LoginContronllerActivity.this.parseError(error, (String) null);
            } else {
                String pwdHash = Base64.encodeToString(MD5.md5(this.etPassword1.getText().toString().trim()).getBytes(), 0).replaceAll("\n", "");
                LoginContronllerActivity loginContronllerActivity = LoginContronllerActivity.this;
                loginContronllerActivity.presentFragment(new ChangePersonalInformationActivity(loginContronllerActivity.newAccount, LoginContronllerActivity.this.currentAccount, this.etPhoneNumber.getText().toString().trim(), pwdHash, (String) null));
            }
            this.reqCheckUNToken = 0;
        }

        /* access modifiers changed from: protected */
        public void checkShowInputIsInvalidTips(TextView tv) {
            super.checkShowInputIsInvalidTips(tv);
            if (tv != null && this.tvPasswordInvalidTips1 != null && tv == this.etPassword1) {
                if (!LoginContronllerActivity.this.checkPasswordRule(tv, false) && this.tvPasswordInvalidTips1.getVisibility() != 0) {
                    this.tvPasswordInvalidTips1.setVisibility(0);
                } else if (LoginContronllerActivity.this.checkPasswordRule(tv, false) && this.tvPasswordInvalidTips1.getVisibility() != 8) {
                    this.tvPasswordInvalidTips1.setVisibility(8);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void changeBtnState() {
            if (!TextUtils.isEmpty(this.etPhoneNumber.getText() + "")) {
                if (!TextUtils.isEmpty(this.etPassword1.getText() + "")) {
                    this.btn.setEnabled(true);
                    return;
                }
            }
            this.btn.setEnabled(false);
        }

        public void onDestroyActivity() {
            TextWatcher textWatcher;
            TextWatcher textWatcher2;
            TextWatcher textWatcher3;
            MryEditText mryEditText = this.etPassword1;
            if (!(mryEditText == null || (textWatcher3 = this.etPasswordWatcher1) == null)) {
                mryEditText.removeTextChangedListener(textWatcher3);
                this.etPasswordWatcher1 = null;
            }
            MryEditText mryEditText2 = this.etPassword2;
            if (!(mryEditText2 == null || (textWatcher2 = this.etPasswordWatcher2) == null)) {
                mryEditText2.removeTextChangedListener(textWatcher2);
                this.etPasswordWatcher2 = null;
            }
            MryEditText mryEditText3 = this.etInviteCode;
            if (!(mryEditText3 == null || (textWatcher = this.etInviteCodeWatcher) == null)) {
                mryEditText3.removeTextChangedListener(textWatcher);
                this.etInviteCodeWatcher = null;
            }
            if (this.reqCheckUNToken != 0) {
                ConnectionsManager.getInstance(LoginContronllerActivity.this.currentAccount).cancelRequest(this.reqCheckUNToken, false);
            }
            super.onDestroyActivity();
        }
    }

    public class TView extends LoginContronllerBaseActivity.ThisView {
        @BindView(2131296567)
        MryEditText etGoogleCode;
        @BindView(2131296568)
        View etGoogleCodedParent;
        @BindView(2131296580)
        MryEditText etPhoneNumber;
        @BindView(2131296581)
        View etPhoneNumberParent;
        private TextWatcher etPhoneWatcher;
        @BindView(2131296725)
        MryAlphaImageView ivClearGoogleCode;
        @BindView(2131296731)
        MryAlphaImageView ivClearPhoneNumber;
        @BindView(2131297420)
        MryRoundButton touristBtn;
        @BindView(2131297569)
        MryTextView tvPhoneNumberInvalidTips;

        public TView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void initView() {
            super.initView();
            this.tvTitle.setTextColor(-1);
            this.touristBtn.setPrimaryRadiusAdjustBoundsFillStyle();
            this.etPhoneNumberParent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(28.0f), AndroidUtilities.alphaColor(0.26f, -15856114)));
            MryEditText mryEditText = this.etPhoneNumber;
            AnonymousClass1 r1 = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    TView tView = TView.this;
                    tView.checkShowInputIsInvalidTips(tView.etPhoneNumber);
                    if (LoginContronllerActivity.this.currentViewIndex == 0) {
                        TView.this.changeBtnState();
                        TView.this.changeClearPhoneNumberBtn();
                        return;
                    }
                    TView.this.etPhoneNumber.setSelection(TView.this.etPhoneNumber.length());
                    TView.this.changeBtnState();
                }
            };
            this.etPhoneWatcher = r1;
            mryEditText.addTextChangedListener(r1);
            loadCurrentState();
            changeBtnState();
        }

        /* access modifiers changed from: protected */
        public void onShowEnd() {
            super.onShowEnd();
            MryEditText mryEditText = this.etPhoneNumber;
            mryEditText.setSelection(mryEditText.length());
            AndroidUtilities.showKeyboard(this.etPhoneNumber);
        }

        /* access modifiers changed from: private */
        public void changeClearPhoneNumberBtn() {
            if (this.etPhoneNumber.getText() == null || this.etPhoneNumber.getText().length() <= 0) {
                this.ivClearPhoneNumber.setVisibility(8);
            } else {
                this.ivClearPhoneNumber.setVisibility(0);
            }
        }

        public void setParams(Bundle params, boolean restore) {
            String inputPhoneNumber;
            super.setParams(params, restore);
            if (params != null && (inputPhoneNumber = params.getString("inputPhoneNumber")) != null) {
                this.etPhoneNumber.setText(inputPhoneNumber);
            }
        }

        public Bundle getParams() {
            if (!(this.params == null || this.etPhoneNumber.getText() == null || this.etPhoneNumber.length() <= 0)) {
                this.params.putString("inputPhoneNumber", this.etPhoneNumber.getText().toString().trim());
            }
            return this.params;
        }

        /* access modifiers changed from: package-private */
        @OnClick({2131296406, 2131296731})
        public void onClick(View v) {
            MryEditText mryEditText;
            int id = v.getId();
            if (id == R.id.btn) {
                checkEnterInfo(false, true, true);
            } else if (id == R.id.ivClearPhoneNumber && (mryEditText = this.etPhoneNumber) != null) {
                mryEditText.setText("");
            }
        }

        /* access modifiers changed from: protected */
        public boolean checkEnterInfo(boolean sendSmsCode, boolean showErrorToast, boolean toNextStep) {
            if (this.etPhoneNumber.getText() == null || this.etPhoneNumber.getText().length() == 0) {
                if (showErrorToast) {
                    LoginContronllerActivity.this.needShowAlert(LocaleController.getString(R.string.PleaseEnterAccountName));
                }
                return false;
            }
            String accountStr = this.etPhoneNumber.getText().toString().trim();
            if (accountStr.length() < 5 || accountStr.length() > 32 || !RegexUtils.firstLetterIsEnglishLetter(accountStr) || !LoginContronllerActivity.isUserName(accountStr)) {
                if (showErrorToast) {
                    LoginContronllerActivity.this.needShowAlert(LocaleController.getString(R.string.ReminderUserNameInvalid));
                }
                return false;
            } else if (!(LoginContronllerActivity.this.getParentActivity() instanceof LaunchActivity)) {
                return true;
            } else {
                for (int a = 0; a < 3; a++) {
                    UserConfig userConfig = UserConfig.getInstance(a);
                    if (userConfig.isClientActivated()) {
                        String username = userConfig.getCurrentUser().username;
                        if (!TextUtils.isEmpty(username) && username.equals(accountStr)) {
                            if (showErrorToast) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) LoginContronllerActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn));
                                builder.setPositiveButton(LocaleController.getString("AccountSwitch", R.string.AccountSwitch), new DialogInterface.OnClickListener(a) {
                                    private final /* synthetic */ int f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        LoginContronllerActivity.TView.this.lambda$checkEnterInfo$0$LoginContronllerActivity$TView(this.f$1, dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                LoginContronllerActivity.this.showDialog(builder.create());
                            }
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        public /* synthetic */ void lambda$checkEnterInfo$0$LoginContronllerActivity$TView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) LoginContronllerActivity.this.getParentActivity()).switchToAccount(num, false);
            }
            LoginContronllerActivity.this.finishFragment();
        }

        /* access modifiers changed from: protected */
        public void checkShowInputIsInvalidTips(TextView tv) {
            if (tv != null && this.tvPhoneNumberInvalidTips != null && tv == this.etPhoneNumber) {
                if (tv.getText() != null && tv.length() > 0) {
                    String accountStr = tv.getText().toString().trim();
                    if (accountStr.length() < 5 || accountStr.length() > 32 || !RegexUtils.firstLetterIsEnglishLetter(accountStr) || !LoginContronllerActivity.isUserName(accountStr)) {
                        if (this.tvPhoneNumberInvalidTips.getVisibility() != 0) {
                            this.tvPhoneNumberInvalidTips.setVisibility(0);
                        }
                    } else if (this.tvPhoneNumberInvalidTips.getVisibility() != 8) {
                        this.tvPhoneNumberInvalidTips.setVisibility(8);
                    }
                } else if (this.tvPhoneNumberInvalidTips.getVisibility() != 0) {
                    this.tvPhoneNumberInvalidTips.setVisibility(0);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void changeBtnState() {
        }

        /* access modifiers changed from: protected */
        public void loadCurrentState() {
            String account_name;
            if (this.etPhoneNumber != null && (account_name = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getString("account_name", (String) null)) != null) {
                this.etPhoneNumber.setText(account_name);
            }
        }

        /* access modifiers changed from: protected */
        public void saveCurrentState() {
            if (!LoginContronllerActivity.this.mIsLoginSuccess && this.etPhoneNumber != null) {
                if ((this.etPhoneNumber.getText() + "").length() != 0) {
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
                    editor.putString("phone_number", this.etPhoneNumber.getText().toString().trim());
                    editor.apply();
                }
            }
        }

        public void saveStateParams(Bundle args) {
            super.saveStateParams(args);
            saveCurrentState();
        }

        public void restoreStateParams(Bundle args) {
            super.restoreStateParams(args);
            loadCurrentState();
        }

        public void onDestroyActivity() {
            TextWatcher textWatcher;
            MryEditText mryEditText = this.etPhoneNumber;
            if (!(mryEditText == null || (textWatcher = this.etPhoneWatcher) == null)) {
                mryEditText.removeTextChangedListener(textWatcher);
                this.etPhoneWatcher = null;
            }
            super.onDestroyActivity();
            if (LoginContronllerActivity.this.mKeyboardChangeListener != null) {
                LoginContronllerActivity.this.mKeyboardChangeListener.destroy();
                LoginContronllerActivity.this.mKeyboardChangeListener = null;
            }
            super.onDestroyActivity();
        }
    }

    public static boolean isUserName(CharSequence str) {
        if (str == null || str.length() <= 0) {
            return false;
        }
        return Pattern.compile(REGEX_USERNAME).matcher(str).matches();
    }
}
