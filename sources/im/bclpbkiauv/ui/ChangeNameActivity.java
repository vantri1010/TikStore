package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
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
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.filter.MaxByteLengthFilter;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryEditText;

public class ChangeNameActivity extends BaseFragment {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public View doneButton;
    private MryEditText etNickname;
    /* access modifiers changed from: private */
    public ImageView ivClear;

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditNickname", R.string.EditNickname));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeNameActivity.this.finishFragment();
                } else if (id == 1) {
                    ChangeNameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString(R.string.Done));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setOnTouchListener($$Lambda$ChangeNameActivity$Ft8EcQlEYen1mw08Mo2zFZcqaBA.INSTANCE);
        FrameLayout nameContainer = new FrameLayout(context2);
        nameContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        nameContainer.setPadding(AndroidUtilities.dp(15.0f), 0, AndroidUtilities.dp(15.0f), 0);
        ((FrameLayout) this.fragmentView).addView(nameContainer, LayoutHelper.createFrame(-1, 55, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        MryEditText mryEditText = new MryEditText(context2);
        this.etNickname = mryEditText;
        mryEditText.setTextSize(16.0f);
        this.etNickname.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.etNickname.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.etNickname.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.etNickname.setMaxLines(1);
        this.etNickname.setLines(1);
        this.etNickname.setSingleLine(true);
        this.etNickname.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.etNickname.setInputType(49152);
        this.etNickname.setImeOptions(6);
        this.etNickname.setHint(LocaleController.getString(R.string.EmptyNicknameTips));
        this.etNickname.setFilters(new InputFilter[]{new MaxByteLengthFilter()});
        this.etNickname.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().trim().length() == 0) {
                    if (ChangeNameActivity.this.doneButton != null) {
                        ChangeNameActivity.this.doneButton.setEnabled(false);
                        ChangeNameActivity.this.doneButton.setAlpha(0.5f);
                    }
                    if (ChangeNameActivity.this.ivClear != null) {
                        ChangeNameActivity.this.ivClear.setVisibility(8);
                        return;
                    }
                    return;
                }
                if (ChangeNameActivity.this.doneButton != null) {
                    ChangeNameActivity.this.doneButton.setEnabled(true);
                    ChangeNameActivity.this.doneButton.setAlpha(1.0f);
                }
                if (ChangeNameActivity.this.ivClear != null) {
                    ChangeNameActivity.this.ivClear.setVisibility(0);
                }
            }
        });
        this.etNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChangeNameActivity.this.lambda$createView$1$ChangeNameActivity(textView, i, keyEvent);
            }
        });
        nameContainer.addView(this.etNickname, LayoutHelper.createFrame(-1, -1, 0, 0, AndroidUtilities.dp(20.0f), 0));
        ImageView imageView = new ImageView(context2);
        this.ivClear = imageView;
        imageView.setImageResource(R.mipmap.ic_clear_remarks);
        nameContainer.addView(this.ivClear, LayoutHelper.createFrame(-2, -2, 21));
        this.ivClear.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChangeNameActivity.this.lambda$createView$2$ChangeNameActivity(view);
            }
        });
        if (user != null) {
            this.etNickname.setText(user.first_name);
            if (this.etNickname.getText() != null) {
                MryEditText mryEditText2 = this.etNickname;
                mryEditText2.setSelection(mryEditText2.getText().length());
            }
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$ChangeNameActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$2$ChangeNameActivity(View v) {
        MryEditText mryEditText = this.etNickname;
        if (mryEditText != null) {
            mryEditText.setText((CharSequence) null);
        }
    }

    public void onResume() {
        MryEditText mryEditText;
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && (mryEditText = this.etNickname) != null) {
            mryEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.etNickname);
        }
    }

    /* access modifiers changed from: private */
    public void saveName() {
        TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (currentUser != null) {
            String newFirst = this.etNickname.getText().toString();
            if (TextUtils.isEmpty(newFirst)) {
                ToastUtils.show((int) R.string.EmptyNameTips);
            } else if (currentUser.first_name == null || !currentUser.first_name.equals(newFirst)) {
                TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
                req.flags = 3;
                req.first_name = newFirst;
                req.last_name = "";
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChangeNameActivity.this.lambda$saveName$4$ChangeNameActivity(tLObject, tL_error);
                    }
                });
            } else {
                finishFragment();
            }
        }
    }

    public /* synthetic */ void lambda$saveName$4$ChangeNameActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChangeNameActivity.this.lambda$null$3$ChangeNameActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$ChangeNameActivity(TLRPC.TL_error error, TLObject response) {
        TLRPC.User user;
        if (error != null) {
            ToastUtils.show((int) R.string.ModifyFail);
            return;
        }
        TLRPC.User newUser = (TLRPC.User) response;
        if (!(newUser == null || (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()))) == null)) {
            user.first_name = newUser.first_name;
            user.last_name = newUser.last_name;
        }
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, 1);
        ToastUtils.show((int) R.string.ModifySuccess);
        finishFragment();
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChangeNameActivity.this.lambda$onTransitionAnimationEnd$5$ChangeNameActivity();
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$5$ChangeNameActivity() {
        MryEditText mryEditText = this.etNickname;
        if (mryEditText != null) {
            mryEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.etNickname);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector)};
    }
}
