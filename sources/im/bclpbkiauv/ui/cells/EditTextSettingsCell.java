package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class EditTextSettingsCell extends FrameLayout {
    private boolean needDivider;
    private EditTextBoldCursor textView;

    public EditTextSettingsCell(Context context) {
        super(context);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.textView = editTextBoldCursor;
        editTextBoldCursor.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable((Drawable) null);
        this.textView.setPadding(0, 0, 0, 0);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(42.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
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
        this.textView.setText(text);
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> arrayList) {
        setEnabled(value);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
