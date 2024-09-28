package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public long dialog_id = getArguments().getLong("dialog_id", 0);
    private View doneButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    private View headerLabelView;
    /* access modifiers changed from: private */
    public int message_id = getArguments().getInt("message_id", 0);

    public ReportOtherActivity(Bundle args) {
        super(args);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_report} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r6) {
                /*
                    r5 = this;
                    r0 = -1
                    if (r6 != r0) goto L_0x000a
                    im.bclpbkiauv.ui.ReportOtherActivity r0 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    r0.finishFragment()
                    goto L_0x00bb
                L_0x000a:
                    r0 = 1
                    if (r6 != r0) goto L_0x00bb
                    im.bclpbkiauv.ui.ReportOtherActivity r0 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r0.firstNameField
                    android.text.Editable r0 = r0.getText()
                    int r0 = r0.length()
                    if (r0 == 0) goto L_0x00bb
                    int r0 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
                    im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
                    im.bclpbkiauv.ui.ReportOtherActivity r1 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    long r1 = r1.dialog_id
                    int r2 = (int) r1
                    im.bclpbkiauv.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer(r2)
                    im.bclpbkiauv.ui.ReportOtherActivity r1 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    int r1 = r1.message_id
                    if (r1 == 0) goto L_0x0065
                    im.bclpbkiauv.tgnet.TLRPC$TL_messages_report r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_report
                    r1.<init>()
                    r1.peer = r0
                    java.util.ArrayList<java.lang.Integer> r2 = r1.id
                    im.bclpbkiauv.ui.ReportOtherActivity r3 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    int r3 = r3.message_id
                    java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                    r2.add(r3)
                    im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonOther r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonOther
                    r2.<init>()
                    im.bclpbkiauv.ui.ReportOtherActivity r3 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r3 = r3.firstNameField
                    android.text.Editable r3 = r3.getText()
                    java.lang.String r3 = r3.toString()
                    r2.text = r3
                    r1.reason = r2
                    goto L_0x0099
                L_0x0065:
                    im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer
                    r1.<init>()
                    im.bclpbkiauv.ui.ReportOtherActivity r2 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    int r2 = r2.currentAccount
                    im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
                    im.bclpbkiauv.ui.ReportOtherActivity r3 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    long r3 = r3.dialog_id
                    int r4 = (int) r3
                    im.bclpbkiauv.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer(r4)
                    r1.peer = r2
                    im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonOther r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonOther
                    r2.<init>()
                    im.bclpbkiauv.ui.ReportOtherActivity r3 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r3 = r3.firstNameField
                    android.text.Editable r3 = r3.getText()
                    java.lang.String r3 = r3.toString()
                    r2.text = r3
                    r1.reason = r2
                    r3 = r1
                L_0x0099:
                    im.bclpbkiauv.ui.ReportOtherActivity r2 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    int r2 = r2.currentAccount
                    im.bclpbkiauv.tgnet.ConnectionsManager r2 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r2)
                    im.bclpbkiauv.ui.-$$Lambda$ReportOtherActivity$1$3FzyeKYBFXcAhYxRHSg1TNWbt3k r3 = im.bclpbkiauv.ui.$$Lambda$ReportOtherActivity$1$3FzyeKYBFXcAhYxRHSg1TNWbt3k.INSTANCE
                    r2.sendRequest(r1, r3)
                    im.bclpbkiauv.ui.ReportOtherActivity r2 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    androidx.fragment.app.FragmentActivity r2 = r2.getParentActivity()
                    if (r2 == 0) goto L_0x00b6
                    r2 = 2131693456(0x7f0f0f90, float:1.901604E38)
                    im.bclpbkiauv.ui.components.toast.ToastUtils.show((int) r2)
                L_0x00b6:
                    im.bclpbkiauv.ui.ReportOtherActivity r2 = im.bclpbkiauv.ui.ReportOtherActivity.this
                    r2.finishFragment()
                L_0x00bb:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ReportOtherActivity.AnonymousClass1.onItemClick(int):void");
            }

            static /* synthetic */ void lambda$onItemClick$0(TLObject response, TLRPC.TL_error error) {
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ReportOtherActivity$eDturl5MqoEF39fLeh96WeBdlM.INSTANCE);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        int i = 3;
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor2.setGravity(i);
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ReportOtherActivity.this.lambda$createView$1$ReportOtherActivity(textView, i, keyEvent);
            }
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
        EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
        editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$ReportOtherActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ReportOtherActivity.this.lambda$onTransitionAnimationEnd$2$ReportOtherActivity();
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$2$ReportOtherActivity() {
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated)};
    }
}
