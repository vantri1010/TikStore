package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class FcDialog extends Dialog {
    private int dialogTextColor;
    private OnCancelClickListener mCancelClickListener;
    private OnConfirmClickListener mConfirmClickListener;
    private Context mContext;
    private FrameLayout mFlContainer;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private MryTextView mTvTitle;

    public interface OnCancelClickListener {
        void onClick(View view);
    }

    public interface OnConfirmClickListener {
        void onClick(View view);
    }

    public FcDialog(Context context) {
        super(context);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        this.dialogTextColor = context.getResources().getColor(R.color.color_text_black_FF444444);
        setContentView(LayoutInflater.from(context).inflate(R.layout.layout_fc_dialog, (ViewGroup) null));
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
        this.mTvCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcDialog.this.lambda$initView$0$FcDialog(view);
            }
        });
        this.mTvConfirm.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcDialog.this.lambda$initView$1$FcDialog(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$FcDialog(View v) {
        OnCancelClickListener onCancelClickListener = this.mCancelClickListener;
        if (onCancelClickListener != null) {
            onCancelClickListener.onClick(v);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$initView$1$FcDialog(View v) {
        OnConfirmClickListener onConfirmClickListener = this.mConfirmClickListener;
        if (onConfirmClickListener != null) {
            onConfirmClickListener.onClick(v);
        }
        dismiss();
    }

    public void setTitle(CharSequence title) {
        this.mTvTitle.setText(title);
    }

    public void addContentView(View v) {
        if (this.mFlContainer.getChildCount() != 0) {
            this.mFlContainer.removeAllViews();
        }
        this.mFlContainer.addView(v, -1, -2);
    }

    public void setContent(CharSequence text) {
        TextView textView = new TextView(this.mContext);
        textView.setText(text);
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        textView.setTextSize(13.0f);
        addContentView(textView);
    }

    public TextView getCancelButton() {
        TextView textView = this.mTvCancel;
        if (!(textView == null || textView.getVisibility() == 0)) {
            this.mTvCancel.setVisibility(0);
        }
        return this.mTvCancel;
    }

    public TextView getConfirmButton() {
        TextView textView = this.mTvConfirm;
        if (!(textView == null || textView.getVisibility() == 0)) {
            this.mTvConfirm.setVisibility(0);
        }
        return this.mTvConfirm;
    }

    public void setConfirmButtonColor(int color) {
        TextView textView = this.mTvConfirm;
        if (textView != null && textView.getVisibility() != 0 && color != 0) {
            this.mTvConfirm.setTextColor(color);
        }
    }

    public void setCancelButtonColor(int color) {
        TextView textView = this.mTvCancel;
        if (textView != null && textView.getVisibility() != 0 && color != 0) {
            this.mTvCancel.setTextColor(color);
        }
    }

    public void setOnCancelClickListener(String text, OnCancelClickListener listener) {
        this.mCancelClickListener = listener;
        if (TextUtils.isEmpty(text)) {
            this.mTvCancel.setText(text);
            this.mTvCancel.setVisibility(8);
            return;
        }
        this.mTvCancel.setText(text);
        this.mTvCancel.setVisibility(0);
    }

    public void setOnConfirmClickListener(String text, OnConfirmClickListener listener) {
        this.mConfirmClickListener = listener;
        if (TextUtils.isEmpty(text)) {
            this.mTvConfirm.setText(text);
            this.mTvConfirm.setVisibility(8);
            return;
        }
        this.mTvConfirm.setText(text);
        this.mTvConfirm.setVisibility(0);
    }
}
