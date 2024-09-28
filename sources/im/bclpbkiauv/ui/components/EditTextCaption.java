package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.TextStyleSpan;

public class EditTextCaption extends EditTextBoldCursor {
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    /* access modifiers changed from: private */
    public boolean copyPasteShowed;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    /* access modifiers changed from: private */
    public View.OnKeyListener mKeyListener;
    private int selectionEnd = -1;
    private int selectionStart = -1;
    private int triesCount = 0;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    public EditTextCaption(Context context) {
        super(context);
    }

    public void setCaption(String value) {
        String str = this.caption;
        if ((str != null && str.length() != 0) || (value != null && value.length() != 0)) {
            String str2 = this.caption;
            if (str2 == null || !str2.equals(value)) {
                this.caption = value;
                if (value != null) {
                    this.caption = value.replace(10, ' ');
                }
                requestLayout();
            }
        }
    }

    public void setDelegate(EditTextCaptionDelegate editTextCaptionDelegate) {
        this.delegate = editTextCaptionDelegate;
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        this.allowTextEntitiesIntersection = value;
    }

    public void makeSelectedBold() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 1;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedItalic() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 2;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedMono() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 4;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedStrike() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 8;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUnderline() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 16;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUrl() {
        int end;
        int start;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("CreateLink", R.string.CreateLink));
        EditTextBoldCursor editText = new EditTextBoldCursor(getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), 1073741824));
            }
        };
        editText.setTextSize(1, 18.0f);
        editText.setText("http://");
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setHintText(LocaleController.getString("URL", R.string.URL));
        editText.setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        editText.setImeOptions(6);
        editText.setBackgroundDrawable((Drawable) null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        builder.setView(editText);
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            start = getSelectionStart();
            end = getSelectionEnd();
        } else {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(start, end, editText) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ EditTextBoldCursor f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                EditTextCaption.this.lambda$makeSelectedUrl$0$EditTextCaption(this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.show().setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                EditTextCaption.lambda$makeSelectedUrl$1(EditTextBoldCursor.this, dialogInterface);
            }
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
            }
            int dp = AndroidUtilities.dp(24.0f);
            layoutParams.leftMargin = dp;
            layoutParams.rightMargin = dp;
            layoutParams.height = AndroidUtilities.dp(36.0f);
            editText.setLayoutParams(layoutParams);
        }
        editText.setSelection(0, editText.getText().length());
    }

    public /* synthetic */ void lambda$makeSelectedUrl$0$EditTextCaption(int start, int end, EditTextBoldCursor editText, DialogInterface dialogInterface, int i) {
        Editable editable = getText();
        CharacterStyle[] spans = (CharacterStyle[]) editable.getSpans(start, end, CharacterStyle.class);
        if (spans != null && spans.length > 0) {
            for (CharacterStyle oldSpan : spans) {
                int spanStart = editable.getSpanStart(oldSpan);
                int spanEnd = editable.getSpanEnd(oldSpan);
                editable.removeSpan(oldSpan);
                if (spanStart < start) {
                    editable.setSpan(oldSpan, spanStart, start, 33);
                }
                if (spanEnd > end) {
                    editable.setSpan(oldSpan, end, spanEnd, 33);
                }
            }
        }
        try {
            editable.setSpan(new URLSpanReplacement(editText.getText().toString()), start, end, 33);
        } catch (Exception e) {
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    static /* synthetic */ void lambda$makeSelectedUrl$1(EditTextBoldCursor editText, DialogInterface dialog) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection((TextStyleSpan) null);
    }

    public void setSelectionOverride(int start, int end) {
        this.selectionStart = start;
        this.selectionEnd = end;
    }

    private void applyTextStyleToSelection(TextStyleSpan span) {
        int end;
        int start;
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            start = getSelectionStart();
            end = getSelectionEnd();
        } else {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        MediaDataController.addStyleToText(span, start, end, getText(), this.allowTextEntitiesIntersection);
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (Build.VERSION.SDK_INT >= 23 || hasWindowFocus || !this.copyPasteShowed) {
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        return new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                boolean unused = EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(mode, menu);
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.menu_regular) {
                    EditTextCaption.this.makeSelectedRegular();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_bold) {
                    EditTextCaption.this.makeSelectedBold();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_italic) {
                    EditTextCaption.this.makeSelectedItalic();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_mono) {
                    EditTextCaption.this.makeSelectedMono();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_link) {
                    EditTextCaption.this.makeSelectedUrl();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_strike) {
                    EditTextCaption.this.makeSelectedStrike();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_underline) {
                    EditTextCaption.this.makeSelectedUnderline();
                    mode.finish();
                    return true;
                } else {
                    try {
                        return callback.onActionItemClicked(mode, item);
                    } catch (Exception e) {
                        return true;
                    }
                }
            }

            public void onDestroyActionMode(ActionMode mode) {
                boolean unused = EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(mode);
            }
        };
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(overrideCallback(callback), type);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int index;
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
            FileLog.e((Throwable) e);
        }
        this.captionLayout = null;
        String str = this.caption;
        if (str != null && str.length() > 0) {
            CharSequence text = getText();
            if (text.length() > 1 && text.charAt(0) == '@' && (index = TextUtils.indexOf(text, ' ')) != -1) {
                TextPaint paint = getPaint();
                CharSequence str2 = text.subSequence(0, index + 1);
                int size = (int) Math.ceil((double) paint.measureText(text, 0, index + 1));
                int width = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                this.userNameLength = str2.length();
                CharSequence captionFinal = TextUtils.ellipsize(this.caption, paint, (float) (width - size), TextUtils.TruncateAt.END);
                this.xOffset = size;
                try {
                    StaticLayout staticLayout = new StaticLayout(captionFinal, getPaint(), width - size, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.captionLayout = staticLayout;
                    if (staticLayout.getLineCount() > 0) {
                        this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                    }
                    this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        }
    }

    public String getCaption() {
        return this.caption;
    }

    public void setOnKeyListener(View.OnKeyListener l) {
        this.mKeyListener = l;
        super.setOnKeyListener(l);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null) {
            return new InnerInputConnection(ic, true);
        }
        return ic;
    }

    private class InnerInputConnection extends InputConnectionWrapper {
        public InnerInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            boolean ret = false;
            if (beforeLength == 1 && afterLength == 0 && EditTextCaption.this.mKeyListener != null) {
                ret = EditTextCaption.this.mKeyListener.onKey(EditTextCaption.this, 67, new KeyEvent(0, 67));
            }
            if (ret || super.deleteSurroundingText(beforeLength, afterLength)) {
                return true;
            }
            return false;
        }
    }

    public void setHintColor(int value) {
        super.setHintColor(value);
        this.hintColor = value;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                Paint paint = getPaint();
                int oldColor = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate((float) this.xOffset, (float) this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(oldColor);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (TextUtils.isEmpty(this.caption)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            info.setHintText(this.caption);
            return;
        }
        info.setText(info.getText() + ", " + this.caption);
    }
}
