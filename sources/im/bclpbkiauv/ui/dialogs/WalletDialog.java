package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class WalletDialog extends Dialog {
    private int dialogTextColor;
    private Context mContext;
    private FrameLayout mFlContainer;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private MryTextView mTvTitle;
    private DialogInterface.OnClickListener negativeButtonListener;
    private DialogInterface.OnClickListener positiveButtonListener;

    public WalletDialog(Context context) {
        super(context);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        this.dialogTextColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
        setContentView(LayoutInflater.from(context).inflate(R.layout.layout_wallet_dialog, (ViewGroup) null));
        setCancelable(true);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable());
        window.setGravity(17);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth();
        window.setAttributes(lp);
        initView(window);
    }

    private void initView(Window window) {
        ((LinearLayout) window.findViewById(R.id.ll_background)).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        ((GradientDrawable) ((LinearLayout) window.findViewById(R.id.ll_bottom_btn)).getDividerDrawable()).setColor(Theme.getColor(Theme.key_windowBackgroundGray));
        window.findViewById(R.id.divider).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.mTvTitle = (MryTextView) window.findViewById(R.id.tv_title);
        this.mFlContainer = (FrameLayout) window.findViewById(R.id.fl_container);
        this.mTvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        this.mTvConfirm = (TextView) window.findViewById(R.id.tv_confirm);
        this.mTvTitle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.mTvConfirm.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.mTvConfirm.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletDialog.this.lambda$initView$0$WalletDialog(view);
            }
        });
        this.mTvCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletDialog.this.lambda$initView$1$WalletDialog(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$WalletDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.positiveButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -1);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$initView$1$WalletDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.negativeButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        dismiss();
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mTvTitle.getLayoutParams();
            lp.height = AndroidUtilities.dp(42.0f);
            this.mTvTitle.setLayoutParams(lp);
            this.mTvTitle.setText(title);
        }
    }

    public void setCustomView(View v) {
        if (this.mFlContainer.getChildCount() != 0) {
            this.mFlContainer.removeAllViews();
        }
        this.mFlContainer.addView(v);
    }

    public void setMessage(CharSequence text) {
        setMessage(text, true, false, false);
    }

    public void setMessage(CharSequence text, boolean alignCenter, boolean isBold) {
        setMessage(text, alignCenter, isBold, false);
    }

    public void setMessage(CharSequence text, boolean alignCenter, boolean isBold, boolean alphaEnable) {
        MryTextView textView = new MryTextView(this.mContext);
        textView.setText(text);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setAlphaEnable(alphaEnable);
        if (alignCenter) {
            textView.setGravity(17);
        }
        textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        if (isBold) {
            textView.setBold();
        }
        textView.setTextColor(this.dialogTextColor);
        textView.setTextSize(14.0f);
        setCustomView(textView);
    }

    public void setMessage(CharSequence text, int size, boolean alignCenter, boolean isBold, boolean alphaEnable) {
        MryTextView textView = new MryTextView(this.mContext);
        textView.setText(text);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setAlphaEnable(alphaEnable);
        if (alignCenter) {
            textView.setGravity(17);
        }
        if (isBold) {
            textView.setBold();
        }
        textView.setTextColor(this.dialogTextColor);
        textView.setTextSize((float) size);
        setCustomView(textView);
    }

    public void setMessage(CharSequence text, int size, int color, boolean alignCenter, boolean isBold, boolean alphaEnable) {
        MryTextView textView = new MryTextView(this.mContext);
        textView.setText(text);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setAlphaEnable(alphaEnable);
        if (alignCenter) {
            textView.setGravity(17);
        }
        if (isBold) {
            textView.setBold();
        }
        textView.setTextColor(color);
        textView.setTextSize((float) size);
        setCustomView(textView);
    }

    public TextView getNegativeButton() {
        TextView textView = this.mTvCancel;
        if (!(textView == null || textView.getVisibility() == 0)) {
            this.mTvCancel.setVisibility(0);
        }
        return this.mTvCancel;
    }

    public TextView getPositiveButton() {
        TextView textView = this.mTvConfirm;
        if (!(textView == null || textView.getVisibility() == 0)) {
            this.mTvConfirm.setVisibility(0);
        }
        return this.mTvConfirm;
    }

    public void setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        this.negativeButtonListener = listener;
        this.mTvCancel.setText(text);
        this.mTvCancel.setVisibility(0);
    }

    public void setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveButtonListener = listener;
        this.mTvConfirm.setText(text);
        this.mTvConfirm.setVisibility(0);
    }

    public void setNegativeButton(String text, int textColor, DialogInterface.OnClickListener listener) {
        this.negativeButtonListener = listener;
        this.mTvCancel.setText(text);
        this.mTvCancel.setTextColor(textColor);
        this.mTvCancel.setVisibility(0);
    }

    public void setPositiveButton(String text, int textColor, DialogInterface.OnClickListener listener) {
        this.positiveButtonListener = listener;
        this.mTvConfirm.setText(text);
        this.mTvConfirm.setTextColor(textColor);
        this.mTvConfirm.setVisibility(0);
    }
}
