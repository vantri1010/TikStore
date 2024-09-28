package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class PollEditTextCell extends FrameLayout {
    private ImageView deleteImageView;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    public PollEditTextCell(Context context, View.OnClickListener onDelete) {
        super(context);
        AnonymousClass1 r0 = new EditTextBoldCursor(context) {
            public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection conn = super.onCreateInputConnection(outAttrs);
                if (PollEditTextCell.this.showNextButton) {
                    outAttrs.imeOptions &= -1073741825;
                }
                return conn;
            }
        };
        this.textView = r0;
        r0.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 14.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable((Drawable) null);
        this.textView.setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | C.ENCODING_PCM_MU_LAW);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (!LocaleController.isRTL || onDelete == null) ? 21.0f : 58.0f, 0.0f, (LocaleController.isRTL || onDelete == null) ? 21.0f : 58.0f, 0.0f));
        if (onDelete != null) {
            ImageView imageView = new ImageView(context);
            this.deleteImageView = imageView;
            imageView.setFocusable(false);
            this.deleteImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setImageResource(R.drawable.msg_panel_clear);
            this.deleteImageView.setOnClickListener(onDelete);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
            addView(this.deleteImageView, LayoutHelper.createFrame(48.0f, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(48.0f, 24.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
            this.textView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824));
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(this.deleteImageView != null ? 79.0f : 42.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        int h = this.textView.getMeasuredHeight();
        setMeasuredDimension(width, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setAlpha(h >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.callOnClick();
        }
    }

    public void setShowNextButton(boolean value) {
        this.showNextButton = value;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
    }

    /* access modifiers changed from: protected */
    public boolean drawDivider() {
        return true;
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public int length() {
        return this.textView.length();
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndHint(String text, String hint, boolean divider) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag((Object) null);
        }
        this.textView.setText(text);
        if (!TextUtils.isEmpty(text)) {
            this.textView.setSelection(text.length());
        }
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> arrayList) {
        setEnabled(value);
    }

    public void setText2(String text) {
        this.textView2.setText(text);
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider && drawDivider()) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
