package com.bigkoo.pickerview.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import java.util.Calendar;

public class TimePickerBuilder {
    private PickerOptions mPickerOptions;

    public TimePickerBuilder(Context context, OnTimeSelectListener listener) {
        PickerOptions pickerOptions = new PickerOptions(2);
        this.mPickerOptions = pickerOptions;
        pickerOptions.context = context;
        this.mPickerOptions.timeSelectListener = listener;
    }

    public TimePickerBuilder setGravity(int gravity) {
        this.mPickerOptions.textGravity = gravity;
        return this;
    }

    public TimePickerBuilder addOnCancelClickListener(View.OnClickListener cancelListener) {
        this.mPickerOptions.cancelListener = cancelListener;
        return this;
    }

    public TimePickerBuilder setType(boolean[] type) {
        this.mPickerOptions.type = type;
        return this;
    }

    public TimePickerBuilder setSubmitText(String textContentConfirm) {
        this.mPickerOptions.textContentConfirm = textContentConfirm;
        return this;
    }

    public TimePickerBuilder isDialog(boolean isDialog) {
        this.mPickerOptions.isDialog = isDialog;
        return this;
    }

    public TimePickerBuilder setCancelText(String textContentCancel) {
        this.mPickerOptions.textContentCancel = textContentCancel;
        return this;
    }

    public TimePickerBuilder setTitleText(String textContentTitle) {
        this.mPickerOptions.textContentTitle = textContentTitle;
        return this;
    }

    public TimePickerBuilder setSubmitColor(int textColorConfirm) {
        this.mPickerOptions.textColorConfirm = textColorConfirm;
        return this;
    }

    public TimePickerBuilder setCancelColor(int textColorCancel) {
        this.mPickerOptions.textColorCancel = textColorCancel;
        return this;
    }

    public TimePickerBuilder setDecorView(ViewGroup decorView) {
        this.mPickerOptions.decorView = decorView;
        return this;
    }

    public TimePickerBuilder setBgColor(int bgColorWheel) {
        this.mPickerOptions.bgColorWheel = bgColorWheel;
        return this;
    }

    public TimePickerBuilder setTitleBgColor(int bgColorTitle) {
        this.mPickerOptions.bgColorTitle = bgColorTitle;
        return this;
    }

    public TimePickerBuilder setTitleColor(int textColorTitle) {
        this.mPickerOptions.textColorTitle = textColorTitle;
        return this;
    }

    public TimePickerBuilder setSubCalSize(int textSizeSubmitCancel) {
        this.mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel;
        return this;
    }

    public TimePickerBuilder setTitleSize(int textSizeTitle) {
        this.mPickerOptions.textSizeTitle = textSizeTitle;
        return this;
    }

    public TimePickerBuilder setContentTextSize(int textSizeContent) {
        this.mPickerOptions.textSizeContent = textSizeContent;
        return this;
    }

    public TimePickerBuilder setItemVisibleCount(int count) {
        this.mPickerOptions.itemsVisibleCount = count;
        return this;
    }

    public TimePickerBuilder isAlphaGradient(boolean isAlphaGradient) {
        this.mPickerOptions.isAlphaGradient = isAlphaGradient;
        return this;
    }

    public TimePickerBuilder setDate(Calendar date) {
        this.mPickerOptions.date = date;
        return this;
    }

    public TimePickerBuilder setLayoutRes(int res, CustomListener customListener) {
        this.mPickerOptions.layoutRes = res;
        this.mPickerOptions.customListener = customListener;
        return this;
    }

    public TimePickerBuilder setRangDate(Calendar startDate, Calendar endDate) {
        this.mPickerOptions.startDate = startDate;
        this.mPickerOptions.endDate = endDate;
        return this;
    }

    public TimePickerBuilder setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }

    public TimePickerBuilder setDividerColor(int dividerColor) {
        this.mPickerOptions.dividerColor = dividerColor;
        return this;
    }

    public TimePickerBuilder setDividerType(WheelView.DividerType dividerType) {
        this.mPickerOptions.dividerType = dividerType;
        return this;
    }

    @Deprecated
    public TimePickerBuilder setBackgroundId(int backgroundId) {
        this.mPickerOptions.outSideColor = backgroundId;
        return this;
    }

    public TimePickerBuilder setOutSideColor(int outSideColor) {
        this.mPickerOptions.outSideColor = outSideColor;
        return this;
    }

    public TimePickerBuilder setTextColorCenter(int textColorCenter) {
        this.mPickerOptions.textColorCenter = textColorCenter;
        return this;
    }

    public TimePickerBuilder setTextColorOut(int textColorOut) {
        this.mPickerOptions.textColorOut = textColorOut;
        return this;
    }

    public TimePickerBuilder isCyclic(boolean cyclic) {
        this.mPickerOptions.cyclic = cyclic;
        return this;
    }

    public TimePickerBuilder setOutSideCancelable(boolean cancelable) {
        this.mPickerOptions.cancelable = cancelable;
        return this;
    }

    public TimePickerBuilder setLunarCalendar(boolean lunarCalendar) {
        this.mPickerOptions.isLunarCalendar = lunarCalendar;
        return this;
    }

    public TimePickerBuilder setLabel(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
        this.mPickerOptions.label_year = label_year;
        this.mPickerOptions.label_month = label_month;
        this.mPickerOptions.label_day = label_day;
        this.mPickerOptions.label_hours = label_hours;
        this.mPickerOptions.label_minutes = label_mins;
        this.mPickerOptions.label_seconds = label_seconds;
        return this;
    }

    public TimePickerBuilder setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day, int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
        this.mPickerOptions.x_offset_year = x_offset_year;
        this.mPickerOptions.x_offset_month = x_offset_month;
        this.mPickerOptions.x_offset_day = x_offset_day;
        this.mPickerOptions.x_offset_hours = x_offset_hours;
        this.mPickerOptions.x_offset_minutes = x_offset_minutes;
        this.mPickerOptions.x_offset_seconds = x_offset_seconds;
        return this;
    }

    public TimePickerBuilder isCenterLabel(boolean isCenterLabel) {
        this.mPickerOptions.isCenterLabel = isCenterLabel;
        return this;
    }

    public TimePickerBuilder setTimeSelectChangeListener(OnTimeSelectChangeListener listener) {
        this.mPickerOptions.timeSelectChangeListener = listener;
        return this;
    }

    public TimePickerView build() {
        return new TimePickerView(this.mPickerOptions);
    }
}
