package im.bclpbkiauv.ui.components.paint.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.Point;
import im.bclpbkiauv.ui.components.Rect;
import im.bclpbkiauv.ui.components.paint.Swatch;
import im.bclpbkiauv.ui.components.paint.views.EntityView;

public class TextPaintView extends EntityView {
    private int baseFontSize;
    /* access modifiers changed from: private */
    public EditTextOutline editText;
    private boolean stroke;
    private Swatch swatch;

    public TextPaintView(Context context, Point position, int fontSize, String text, Swatch swatch2, boolean stroke2) {
        super(context, position);
        this.baseFontSize = fontSize;
        EditTextOutline editTextOutline = new EditTextOutline(context);
        this.editText = editTextOutline;
        editTextOutline.setBackgroundColor(0);
        this.editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        this.editText.setClickable(false);
        this.editText.setEnabled(false);
        this.editText.setTextSize(0, (float) this.baseFontSize);
        this.editText.setText(text);
        this.editText.setTextColor(swatch2.color);
        this.editText.setTypeface((Typeface) null, 1);
        this.editText.setGravity(17);
        this.editText.setHorizontallyScrolling(false);
        this.editText.setImeOptions(C.ENCODING_PCM_MU_LAW);
        this.editText.setFocusableInTouchMode(true);
        EditTextOutline editTextOutline2 = this.editText;
        editTextOutline2.setInputType(editTextOutline2.getInputType() | 16384);
        addView(this.editText, LayoutHelper.createFrame(-2, -2, 51));
        if (Build.VERSION.SDK_INT >= 23) {
            this.editText.setBreakStrategy(0);
        }
        setSwatch(swatch2);
        setStroke(stroke2);
        updatePosition();
        this.editText.addTextChangedListener(new TextWatcher() {
            private int beforeCursorPosition = 0;
            private String text;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.text = s.toString();
                this.beforeCursorPosition = start;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                TextPaintView.this.editText.removeTextChangedListener(this);
                if (TextPaintView.this.editText.getLineCount() > 9) {
                    TextPaintView.this.editText.setText(this.text);
                    TextPaintView.this.editText.setSelection(this.beforeCursorPosition);
                }
                TextPaintView.this.editText.addTextChangedListener(this);
            }
        });
    }

    public TextPaintView(Context context, TextPaintView textPaintView, Point position) {
        this(context, position, textPaintView.baseFontSize, textPaintView.getText(), textPaintView.getSwatch(), textPaintView.stroke);
        setRotation(textPaintView.getRotation());
        setScale(textPaintView.getScale());
    }

    public void setMaxWidth(int maxWidth) {
        this.editText.setMaxWidth(maxWidth);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updatePosition();
    }

    public String getText() {
        return this.editText.getText().toString();
    }

    public void setText(String text) {
        this.editText.setText(text);
    }

    public View getFocusedView() {
        return this.editText;
    }

    public void beginEditing() {
        this.editText.setEnabled(true);
        this.editText.setClickable(true);
        this.editText.requestFocus();
        EditTextOutline editTextOutline = this.editText;
        editTextOutline.setSelection(editTextOutline.getText().length());
    }

    public void endEditing() {
        this.editText.clearFocus();
        this.editText.setEnabled(false);
        this.editText.setClickable(false);
        updateSelectionView();
    }

    public Swatch getSwatch() {
        return this.swatch;
    }

    public void setSwatch(Swatch swatch2) {
        this.swatch = swatch2;
        updateColor();
    }

    public void setStroke(boolean stroke2) {
        this.stroke = stroke2;
        updateColor();
    }

    private void updateColor() {
        if (this.stroke) {
            this.editText.setTextColor(-1);
            this.editText.setStrokeColor(this.swatch.color);
            this.editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            return;
        }
        this.editText.setTextColor(this.swatch.color);
        this.editText.setStrokeColor(0);
        this.editText.setShadowLayer(8.0f, 0.0f, 2.0f, -1442840576);
    }

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        float scale = ((ViewGroup) getParent()).getScaleX();
        float width = (((float) getWidth()) * getScale()) + (((float) AndroidUtilities.dp(46.0f)) / scale);
        float height = (((float) getHeight()) * getScale()) + (((float) AndroidUtilities.dp(20.0f)) / scale);
        return new Rect((this.position.x - (width / 2.0f)) * scale, (this.position.y - (height / 2.0f)) * scale, width * scale, height * scale);
    }

    /* access modifiers changed from: protected */
    public TextViewSelectionView createSelectionView() {
        return new TextViewSelectionView(getContext());
    }

    public class TextViewSelectionView extends EntityView.SelectionView {
        public TextViewSelectionView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float x, float y) {
            float radius = (float) AndroidUtilities.dp(19.5f);
            float inset = radius + ((float) AndroidUtilities.dp(1.0f));
            float width = ((float) getWidth()) - (inset * 2.0f);
            float height = ((float) getHeight()) - (inset * 2.0f);
            float middle = (height / 2.0f) + inset;
            if (x > inset - radius && y > middle - radius && x < inset + radius && y < middle + radius) {
                return 1;
            }
            if (x > (inset + width) - radius && y > middle - radius && x < inset + width + radius && y < middle + radius) {
                return 2;
            }
            if (x <= inset || x >= width || y <= inset || y >= height) {
                return 0;
            }
            return 3;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            float space = (float) AndroidUtilities.dp(3.0f);
            float length = (float) AndroidUtilities.dp(3.0f);
            float thickness = (float) AndroidUtilities.dp(1.0f);
            float radius = (float) AndroidUtilities.dp(4.5f);
            float inset = radius + thickness + ((float) AndroidUtilities.dp(15.0f));
            float width = ((float) getWidth()) - (inset * 2.0f);
            float height = ((float) getHeight()) - (inset * 2.0f);
            int xCount = (int) Math.floor((double) (width / (space + length)));
            float xGap = (float) Math.ceil((double) (((width - (((float) xCount) * (space + length))) + space) / 2.0f));
            int i = 0;
            while (i < xCount) {
                float x = xGap + inset + (((float) i) * (length + space));
                int i2 = i;
                float xGap2 = xGap;
                canvas.drawRect(x, inset - (thickness / 2.0f), x + length, inset + (thickness / 2.0f), this.paint);
                canvas.drawRect(x, (inset + height) - (thickness / 2.0f), x + length, inset + height + (thickness / 2.0f), this.paint);
                i = i2 + 1;
                xGap = xGap2;
                xCount = xCount;
            }
            int i3 = i;
            float f = xGap;
            int i4 = xCount;
            int yCount = (int) Math.floor((double) (height / (space + length)));
            float yGap = (float) Math.ceil((double) (((height - (((float) yCount) * (space + length))) + space) / 2.0f));
            int i5 = 0;
            while (i5 < yCount) {
                float y = yGap + inset + (((float) i5) * (length + space));
                int i6 = i5;
                float yGap2 = yGap;
                canvas.drawRect(inset - (thickness / 2.0f), y, inset + (thickness / 2.0f), y + length, this.paint);
                canvas.drawRect((inset + width) - (thickness / 2.0f), y, inset + width + (thickness / 2.0f), y + length, this.paint);
                i5 = i6 + 1;
                yGap = yGap2;
                yCount = yCount;
            }
            canvas2.drawCircle(inset, (height / 2.0f) + inset, radius, this.dotPaint);
            canvas2.drawCircle(inset, (height / 2.0f) + inset, radius, this.dotStrokePaint);
            canvas2.drawCircle(inset + width, (height / 2.0f) + inset, radius, this.dotPaint);
            canvas2.drawCircle(inset + width, (height / 2.0f) + inset, radius, this.dotStrokePaint);
        }
    }
}
