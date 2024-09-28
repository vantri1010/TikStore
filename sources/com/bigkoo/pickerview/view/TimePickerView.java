package com.bigkoo.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import java.text.ParseException;
import java.util.Calendar;

public class TimePickerView extends BasePickerView implements View.OnClickListener {
    private static final String TAG_CANCEL = "cancel";
    private static final String TAG_SUBMIT = "submit";
    /* access modifiers changed from: private */
    public WheelTime wheelTime;

    public TimePickerView(PickerOptions pickerOptions) {
        super(pickerOptions.context);
        this.mPickerOptions = pickerOptions;
        initView(pickerOptions.context);
    }

    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();
        if (this.mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(R.layout.pickerview_time, this.contentContainer);
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);
            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnSubmit.setText(TextUtils.isEmpty(this.mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : this.mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(this.mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : this.mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(this.mPickerOptions.textContentTitle) ? "" : this.mPickerOptions.textContentTitle);
            btnSubmit.setTextColor(this.mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(this.mPickerOptions.textColorCancel);
            tvTitle.setTextColor(this.mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(this.mPickerOptions.bgColorTitle);
            btnSubmit.setTextSize((float) this.mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize((float) this.mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize((float) this.mPickerOptions.textSizeTitle);
        } else {
            this.mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(this.mPickerOptions.layoutRes, this.contentContainer));
        }
        LinearLayout timePickerView = (LinearLayout) findViewById(R.id.timepicker);
        timePickerView.setBackgroundColor(this.mPickerOptions.bgColorWheel);
        initWheelTime(timePickerView);
    }

    private void initWheelTime(LinearLayout timePickerView) {
        this.wheelTime = new WheelTime(timePickerView, this.mPickerOptions.type, this.mPickerOptions.textGravity, this.mPickerOptions.textSizeContent);
        if (this.mPickerOptions.timeSelectChangeListener != null) {
            this.wheelTime.setSelectChangeCallback(new ISelectTimeCallback() {
                public void onTimeSelectChanged() {
                    try {
                        TimePickerView.this.mPickerOptions.timeSelectChangeListener.onTimeSelectChanged(WheelTime.dateFormat.parse(TimePickerView.this.wheelTime.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        this.wheelTime.setLunarMode(this.mPickerOptions.isLunarCalendar);
        if (!(this.mPickerOptions.startYear == 0 || this.mPickerOptions.endYear == 0 || this.mPickerOptions.startYear > this.mPickerOptions.endYear)) {
            setRange();
        }
        if (this.mPickerOptions.startDate == null || this.mPickerOptions.endDate == null) {
            if (this.mPickerOptions.startDate != null) {
                if (this.mPickerOptions.startDate.get(1) >= 1900) {
                    setRangDate();
                } else {
                    throw new IllegalArgumentException("The startDate can not as early as 1900");
                }
            } else if (this.mPickerOptions.endDate == null) {
                setRangDate();
            } else if (this.mPickerOptions.endDate.get(1) <= 2100) {
                setRangDate();
            } else {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            }
        } else if (this.mPickerOptions.startDate.getTimeInMillis() <= this.mPickerOptions.endDate.getTimeInMillis()) {
            setRangDate();
        } else {
            throw new IllegalArgumentException("startDate can't be later than endDate");
        }
        setTime();
        this.wheelTime.setLabels(this.mPickerOptions.label_year, this.mPickerOptions.label_month, this.mPickerOptions.label_day, this.mPickerOptions.label_hours, this.mPickerOptions.label_minutes, this.mPickerOptions.label_seconds);
        this.wheelTime.setTextXOffset(this.mPickerOptions.x_offset_year, this.mPickerOptions.x_offset_month, this.mPickerOptions.x_offset_day, this.mPickerOptions.x_offset_hours, this.mPickerOptions.x_offset_minutes, this.mPickerOptions.x_offset_seconds);
        this.wheelTime.setItemsVisible(this.mPickerOptions.itemsVisibleCount);
        this.wheelTime.setAlphaGradient(this.mPickerOptions.isAlphaGradient);
        setOutSideCancelable(this.mPickerOptions.cancelable);
        this.wheelTime.setCyclic(this.mPickerOptions.cyclic);
        this.wheelTime.setDividerColor(this.mPickerOptions.dividerColor);
        this.wheelTime.setDividerType(this.mPickerOptions.dividerType);
        this.wheelTime.setLineSpacingMultiplier(this.mPickerOptions.lineSpacingMultiplier);
        this.wheelTime.setTextColorOut(this.mPickerOptions.textColorOut);
        this.wheelTime.setTextColorCenter(this.mPickerOptions.textColorCenter);
        this.wheelTime.isCenterLabel(this.mPickerOptions.isCenterLabel);
    }

    public void setDate(Calendar date) {
        this.mPickerOptions.date = date;
        setTime();
    }

    private void setRange() {
        this.wheelTime.setStartYear(this.mPickerOptions.startYear);
        this.wheelTime.setEndYear(this.mPickerOptions.endYear);
    }

    private void setRangDate() {
        this.wheelTime.setRangDate(this.mPickerOptions.startDate, this.mPickerOptions.endDate);
        initDefaultSelectedDate();
    }

    private void initDefaultSelectedDate() {
        if (this.mPickerOptions.startDate == null || this.mPickerOptions.endDate == null) {
            if (this.mPickerOptions.startDate != null) {
                this.mPickerOptions.date = this.mPickerOptions.startDate;
            } else if (this.mPickerOptions.endDate != null) {
                this.mPickerOptions.date = this.mPickerOptions.endDate;
            }
        } else if (this.mPickerOptions.date == null || this.mPickerOptions.date.getTimeInMillis() < this.mPickerOptions.startDate.getTimeInMillis() || this.mPickerOptions.date.getTimeInMillis() > this.mPickerOptions.endDate.getTimeInMillis()) {
            this.mPickerOptions.date = this.mPickerOptions.startDate;
        }
    }

    private void setTime() {
        int month;
        int day;
        int hours;
        int minute;
        int seconds;
        int year;
        Calendar calendar = Calendar.getInstance();
        if (this.mPickerOptions.date == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(1);
            month = calendar.get(2);
            day = calendar.get(5);
            hours = calendar.get(11);
            minute = calendar.get(12);
            seconds = calendar.get(13);
        } else {
            year = this.mPickerOptions.date.get(1);
            month = this.mPickerOptions.date.get(2);
            day = this.mPickerOptions.date.get(5);
            hours = this.mPickerOptions.date.get(11);
            minute = this.mPickerOptions.date.get(12);
            seconds = this.mPickerOptions.date.get(13);
        }
        this.wheelTime.setPicker(year, month, day, hours, minute, seconds);
    }

    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            returnData();
        } else if (tag.equals(TAG_CANCEL) && this.mPickerOptions.cancelListener != null) {
            this.mPickerOptions.cancelListener.onClick(v);
        }
        dismiss();
    }

    public void returnData() {
        if (this.mPickerOptions.timeSelectListener != null) {
            try {
                this.mPickerOptions.timeSelectListener.onTimeSelect(WheelTime.dateFormat.parse(this.wheelTime.getTime()), this.clickView);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    public void setLunarCalendar(boolean lunar) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(WheelTime.dateFormat.parse(this.wheelTime.getTime()));
            int year = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            int hours = calendar.get(11);
            int minute = calendar.get(12);
            int seconds = calendar.get(13);
            try {
                this.wheelTime.setLunarMode(lunar);
                this.wheelTime.setLabels(this.mPickerOptions.label_year, this.mPickerOptions.label_month, this.mPickerOptions.label_day, this.mPickerOptions.label_hours, this.mPickerOptions.label_minutes, this.mPickerOptions.label_seconds);
                this.wheelTime.setPicker(year, month, day, hours, minute, seconds);
            } catch (ParseException e) {
                e = e;
            }
        } catch (ParseException e2) {
            e = e2;
            boolean z = lunar;
            e.printStackTrace();
        }
    }

    public boolean isLunarCalendar() {
        return this.wheelTime.isLunarMode();
    }

    public boolean isDialog() {
        return this.mPickerOptions.isDialog;
    }
}
