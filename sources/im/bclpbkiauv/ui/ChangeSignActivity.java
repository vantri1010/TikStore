package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.OnClick;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class ChangeSignActivity extends BaseFragment {
    @BindView(2131296435)
    MryRoundButton mBtnSubmit;
    @BindView(2131296607)
    MryEditText mEtSignature;
    @BindView(2131297210)
    RelativeLayout mRlSignatureContainer;
    @BindView(2131297743)
    MryTextView mTvCount;
    private TextWatcher mWatcher;
    private final TLRPCContacts.CL_userFull_v1 userFull;

    public ChangeSignActivity(TLRPCContacts.CL_userFull_v1 userFull2) {
        this.userFull = userFull2;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_change_sign, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        useButterKnife();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.SetSignature));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeSignActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.mRlSignatureContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        MryEditText mryEditText = this.mEtSignature;
        AnonymousClass2 r1 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                MryTextView mryTextView = ChangeSignActivity.this.mTvCount;
                mryTextView.setText(s.length() + "/30");
                ChangeSignActivity.this.mBtnSubmit.setEnabled(TextUtils.isEmpty(s) ^ true);
            }
        };
        this.mWatcher = r1;
        mryEditText.addTextChangedListener(r1);
        this.mBtnSubmit.setPrimaryRadiusAdjustBoundsFillStyle();
        this.mBtnSubmit.setText(LocaleController.getString("Submit", R.string.Submit));
        this.mBtnSubmit.setBackgroundColor(-12862209);
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
        if (cL_userFull_v1 != null) {
            this.mBtnSubmit.setEnabled(!TextUtils.isEmpty(cL_userFull_v1.about));
            this.mEtSignature.setText(this.userFull.about != null ? this.userFull.about : "");
        }
    }

    @OnClick({2131296435})
    public void onViewClicked() {
        String signature = this.mEtSignature.getText().toString();
        if (!TextUtils.isEmpty(signature)) {
            submit(signature);
        } else {
            ToastUtils.show((CharSequence) "Sign not empty...");
        }
    }

    private void submit(String signature) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
        req.about = signature;
        req.flags = 4 | req.flags;
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, signature, req) {
            private final /* synthetic */ XAlertDialog f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ TLRPC.TL_account_updateProfile f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeSignActivity.this.lambda$submit$2$ChangeSignActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                ChangeSignActivity.this.lambda$submit$3$ChangeSignActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$submit$2$ChangeSignActivity(XAlertDialog progressDialog, String signature, TLRPC.TL_account_updateProfile req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, signature, (TLRPC.User) response) {
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ TLRPC.User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChangeSignActivity.this.lambda$null$0$ChangeSignActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, req) {
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ TLRPC.TL_error f$2;
                private final /* synthetic */ TLRPC.TL_account_updateProfile f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChangeSignActivity.this.lambda$null$1$ChangeSignActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$ChangeSignActivity(XAlertDialog progressDialog, String signature, TLRPC.User user) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.userFull.about = signature;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userFullInfoDidLoad, Integer.valueOf(user.id), this.userFull, null);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$1$ChangeSignActivity(XAlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_account_updateProfile req) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    public /* synthetic */ void lambda$submit$3$ChangeSignActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.mEtSignature.requestFocus();
            AndroidUtilities.showKeyboard(this.mEtSignature);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.mEtSignature.requestFocus();
            AndroidUtilities.showKeyboard(this.mEtSignature);
        }
    }

    public void onFragmentDestroy() {
        TextWatcher textWatcher;
        MryEditText mryEditText = this.mEtSignature;
        if (!(mryEditText == null || (textWatcher = this.mWatcher) == null)) {
            mryEditText.removeTextChangedListener(textWatcher);
        }
        super.onFragmentDestroy();
    }
}
