package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import im.bclpbkiauv.ui.components.CheckBoxBase;

public class CheckBox2 extends View {
    private CheckBoxBase checkBoxBase;

    public CheckBox2(Context context) {
        this(context, 21);
    }

    public CheckBox2(Context context, int sz) {
        super(context);
        this.checkBoxBase = new CheckBoxBase(this, sz);
    }

    public CheckBox2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBox2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.checkBoxBase = new CheckBoxBase(this, 21);
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate delegate) {
        this.checkBoxBase.setProgressDelegate(delegate);
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        this.checkBoxBase.setChecked(num, checked, animated);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBoxBase.setChecked(checked, animated);
    }

    public void setNum(int num) {
        this.checkBoxBase.setNum(num);
    }

    public boolean isChecked() {
        return this.checkBoxBase.isChecked();
    }

    public void setColor(String background, String background2, String check) {
        this.checkBoxBase.setColor(background, background2, check);
    }

    public void setEnabled(boolean enabled) {
        this.checkBoxBase.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void setDrawUnchecked(boolean value) {
        this.checkBoxBase.setDrawUnchecked(value);
    }

    public void setDrawBackgroundAsArc(int type) {
        this.checkBoxBase.setDrawBackgroundAsArc(type);
    }

    public float getProgress() {
        return this.checkBoxBase.getProgress();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkBoxBase.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.checkBoxBase.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.checkBoxBase.setBounds(0, 0, right - left, bottom - top);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.checkBoxBase.draw(canvas);
    }
}
