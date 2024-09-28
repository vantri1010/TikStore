package com.bigkoo.pickerview.builder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.view.WheelView;

public class OptionsPickerBuilder {
    private PickerOptions mPickerOptions;

    public OptionsPickerBuilder(Context context, OnOptionsSelectListener listener) {
        PickerOptions pickerOptions = new PickerOptions(1);
        this.mPickerOptions = pickerOptions;
        pickerOptions.context = context;
        this.mPickerOptions.optionsSelectListener = listener;
    }

    public OptionsPickerBuilder setSubmitText(String textContentConfirm) {
        this.mPickerOptions.textContentConfirm = textContentConfirm;
        return this;
    }

    public OptionsPickerBuilder setCancelText(String textContentCancel) {
        this.mPickerOptions.textContentCancel = textContentCancel;
        return this;
    }

    public OptionsPickerBuilder setTitleText(String textContentTitle) {
        this.mPickerOptions.textContentTitle = textContentTitle;
        return this;
    }

    public OptionsPickerBuilder isDialog(boolean isDialog) {
        this.mPickerOptions.isDialog = isDialog;
        return this;
    }

    public OptionsPickerBuilder addOnCancelClickListener(View.OnClickListener cancelListener) {
        this.mPickerOptions.cancelListener = cancelListener;
        return this;
    }

    public OptionsPickerBuilder setSubmitColor(int textColorConfirm) {
        this.mPickerOptions.textColorConfirm = textColorConfirm;
        return this;
    }

    public OptionsPickerBuilder setCancelColor(int textColorCancel) {
        this.mPickerOptions.textColorCancel = textColorCancel;
        return this;
    }

    @Deprecated
    public OptionsPickerBuilder setBackgroundId(int backgroundId) {
        this.mPickerOptions.outSideColor = backgroundId;
        return this;
    }

    public OptionsPickerBuilder setOutSideColor(int outSideColor) {
        this.mPickerOptions.outSideColor = outSideColor;
        return this;
    }

    public OptionsPickerBuilder setDecorView(ViewGroup decorView) {
        this.mPickerOptions.decorView = decorView;
        return this;
    }

    public OptionsPickerBuilder setLayoutRes(int res, CustomListener listener) {
        this.mPickerOptions.layoutRes = res;
        this.mPickerOptions.customListener = listener;
        return this;
    }

    public OptionsPickerBuilder setBgColor(int bgColorWheel) {
        this.mPickerOptions.bgColorWheel = bgColorWheel;
        return this;
    }

    public OptionsPickerBuilder setTitleBgColor(int bgColorTitle) {
        this.mPickerOptions.bgColorTitle = bgColorTitle;
        return this;
    }

    public OptionsPickerBuilder setTitleColor(int textColorTitle) {
        this.mPickerOptions.textColorTitle = textColorTitle;
        return this;
    }

    public OptionsPickerBuilder setSubCalSize(int textSizeSubmitCancel) {
        this.mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel;
        return this;
    }

    public OptionsPickerBuilder setTitleSize(int textSizeTitle) {
        this.mPickerOptions.textSizeTitle = textSizeTitle;
        return this;
    }

    public OptionsPickerBuilder setContentTextSize(int textSizeContent) {
        this.mPickerOptions.textSizeContent = textSizeContent;
        return this;
    }

    public OptionsPickerBuilder setOutSideCancelable(boolean cancelable) {
        this.mPickerOptions.cancelable = cancelable;
        return this;
    }

    public OptionsPickerBuilder setLabels(String label1, String label2, String label3) {
        this.mPickerOptions.label1 = label1;
        this.mPickerOptions.label2 = label2;
        this.mPickerOptions.label3 = label3;
        return this;
    }

    public OptionsPickerBuilder setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }

    public OptionsPickerBuilder setDividerColor(int dividerColor) {
        this.mPickerOptions.dividerColor = dividerColor;
        return this;
    }

    public OptionsPickerBuilder setDividerType(WheelView.DividerType dividerType) {
        this.mPickerOptions.dividerType = dividerType;
        return this;
    }

    public OptionsPickerBuilder setTextColorCenter(int textColorCenter) {
        this.mPickerOptions.textColorCenter = textColorCenter;
        return this;
    }

    public OptionsPickerBuilder setTextColorOut(int textColorOut) {
        this.mPickerOptions.textColorOut = textColorOut;
        return this;
    }

    public OptionsPickerBuilder setTypeface(Typeface font) {
        this.mPickerOptions.font = font;
        return this;
    }

    public OptionsPickerBuilder setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        this.mPickerOptions.cyclic1 = cyclic1;
        this.mPickerOptions.cyclic2 = cyclic2;
        this.mPickerOptions.cyclic3 = cyclic3;
        return this;
    }

    public OptionsPickerBuilder setSelectOptions(int option1) {
        this.mPickerOptions.option1 = option1;
        return this;
    }

    public OptionsPickerBuilder setSelectOptions(int option1, int option2) {
        this.mPickerOptions.option1 = option1;
        this.mPickerOptions.option2 = option2;
        return this;
    }

    public OptionsPickerBuilder setSelectOptions(int option1, int option2, int option3) {
        this.mPickerOptions.option1 = option1;
        this.mPickerOptions.option2 = option2;
        this.mPickerOptions.option3 = option3;
        return this;
    }

    public OptionsPickerBuilder setTextXOffset(int xoffset_one, int xoffset_two, int xoffset_three) {
        this.mPickerOptions.x_offset_one = xoffset_one;
        this.mPickerOptions.x_offset_two = xoffset_two;
        this.mPickerOptions.x_offset_three = xoffset_three;
        return this;
    }

    public OptionsPickerBuilder isCenterLabel(boolean isCenterLabel) {
        this.mPickerOptions.isCenterLabel = isCenterLabel;
        return this;
    }

    public OptionsPickerBuilder setItemVisibleCount(int count) {
        this.mPickerOptions.itemsVisibleCount = count;
        return this;
    }

    public OptionsPickerBuilder isAlphaGradient(boolean isAlphaGradient) {
        this.mPickerOptions.isAlphaGradient = isAlphaGradient;
        return this;
    }

    public OptionsPickerBuilder isRestoreItem(boolean isRestoreItem) {
        this.mPickerOptions.isRestoreItem = isRestoreItem;
        return this;
    }

    public OptionsPickerBuilder setOptionsSelectChangeListener(OnOptionsSelectChangeListener listener) {
        this.mPickerOptions.optionsSelectChangeListener = listener;
        return this;
    }

    public <T> OptionsPickerView<T> build() {
        return new OptionsPickerView<>(this.mPickerOptions);
    }
}
