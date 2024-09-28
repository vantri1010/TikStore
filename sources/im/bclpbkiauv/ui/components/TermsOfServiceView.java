package im.bclpbkiauv.ui.components;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;

public class TermsOfServiceView extends FrameLayout {
    private int currentAccount;
    private TLRPC.TL_help_termsOfService currentTos;
    private TermsOfServiceViewDelegate delegate;
    private ScrollView scrollView;
    private TextView textView;
    private TextView titleTextView;

    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);

        void onDeclineTerms(int i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TermsOfServiceView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = Build.VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        if (Build.VERSION.SDK_INT >= 21) {
            View view = new View(context2);
            view.setBackgroundColor(-16777216);
            addView(view, new FrameLayout.LayoutParams(-1, AndroidUtilities.statusBarHeight));
        }
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(R.mipmap.ic_logo);
        addView(imageView, LayoutHelper.createFrame(-2.0f, -2.0f, 49, 0.0f, (float) (top + 30), 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.titleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setTextSize(1, 17.0f);
        this.titleTextView.setGravity(51);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", R.string.PrivacyPolicyAndTerms));
        addView(this.titleTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 27.0f, (float) (top + 126), 27.0f, 75.0f));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView2, Theme.getColor(Theme.key_actionBarDefault));
        addView(this.scrollView, LayoutHelper.createFrame(-2.0f, -1.0f, 51, 27.0f, (float) (top + 160), 27.0f, 75.0f));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.scrollView.addView(this.textView, new FrameLayout.LayoutParams(-2, -2));
        TextView declineTextView = new TextView(context2);
        declineTextView.setText(LocaleController.getString("Decline", R.string.Decline).toUpperCase());
        declineTextView.setGravity(17);
        declineTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        declineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        declineTextView.setTextSize(1, 16.0f);
        declineTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(declineTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        declineTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TermsOfServiceView.this.lambda$new$4$TermsOfServiceView(view);
            }
        });
        TextView acceptTextView = new TextView(context2);
        acceptTextView.setText(LocaleController.getString("Accept", R.string.Accept).toUpperCase());
        acceptTextView.setGravity(17);
        acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        acceptTextView.setTextColor(-1);
        acceptTextView.setTextSize(1, 16.0f);
        acceptTextView.setBackgroundResource(R.drawable.regbtn_states);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            acceptTextView.setStateListAnimator(animator);
        }
        acceptTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(acceptTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        acceptTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TermsOfServiceView.this.lambda$new$6$TermsOfServiceView(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$4$TermsOfServiceView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", R.string.DeclineDeactivate), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.lambda$null$3$TermsOfServiceView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Back", R.string.Back), (DialogInterface.OnClickListener) null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", R.string.TosUpdateDecline));
        builder.show();
    }

    public /* synthetic */ void lambda$null$3$TermsOfServiceView(DialogInterface dialog, int which) {
        AlertDialog.Builder builder12 = new AlertDialog.Builder(getContext());
        builder12.setMessage(LocaleController.getString("TosDeclineDeleteAccount", R.string.TosDeclineDeleteAccount));
        builder12.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder12.setPositiveButton(LocaleController.getString("Deactivate", R.string.Deactivate), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.lambda$null$2$TermsOfServiceView(dialogInterface, i);
            }
        });
        builder12.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder12.show();
    }

    public /* synthetic */ void lambda$null$2$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog progressDialog = new AlertDialog(getContext(), 3);
        progressDialog.setCanCancel(false);
        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
        req.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TermsOfServiceView.this.lambda$null$1$TermsOfServiceView(this.f$1, tLObject, tL_error);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$null$1$TermsOfServiceView(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response, error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TermsOfServiceView.this.lambda$null$0$TermsOfServiceView(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$TermsOfServiceView(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
        } else if (error == null || error.code != -1000) {
            String errorText = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred);
            if (error != null) {
                errorText = errorText + "\n" + error.text;
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder1.setMessage(errorText);
            builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder1.show();
        }
    }

    public /* synthetic */ void lambda$new$6$TermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", R.string.TosAgeTitle));
            builder.setPositiveButton(LocaleController.getString("Agree", R.string.Agree), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TermsOfServiceView.this.lambda$null$5$TermsOfServiceView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            builder.setMessage(LocaleController.formatString("TosAgeText", R.string.TosAgeText, LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm)));
            builder.show();
            return;
        }
        accept();
    }

    public /* synthetic */ void lambda$null$5$TermsOfServiceView(DialogInterface dialog, int which) {
        accept();
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TLRPC.TL_help_acceptTermsOfService req = new TLRPC.TL_help_acceptTermsOfService();
        req.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$TermsOfServiceView$NnOYDJt_jM22ed9HntfsghYre_g.INSTANCE);
    }

    static /* synthetic */ void lambda$accept$7(TLObject response, TLRPC.TL_error error) {
    }

    public void show(int account, TLRPC.TL_help_termsOfService tos) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(tos.text);
        MessageObject.addEntitiesToText(builder, tos.entities, false, 0, false, false, false);
        this.textView.setText(builder);
        this.currentTos = tos;
        this.currentAccount = account;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(this.titleTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        ((FrameLayout.LayoutParams) this.scrollView.getLayoutParams()).topMargin = AndroidUtilities.dp(156.0f) + this.titleTextView.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }
}
